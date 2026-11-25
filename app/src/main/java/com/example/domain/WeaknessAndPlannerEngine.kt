package com.example.domain

import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.local.ChapterProgressEntity
import com.example.data.local.ConceptStatEntity

/**
 * Intelligent Concept-Level Weakness Detector & Automatic Exam-Countdown Study Planner.
 * Tracks exact concepts causing errors rather than labelling entire chapters as "weak",
 * and dynamically allocates daily study minutes based on days remaining before SSLC boards.
 */
object WeaknessAndPlannerEngine {

    fun analyzeConceptWeaknesses(
        conceptStats: List<ConceptStatEntity>
    ): List<ConceptWeaknessInsight> {
        return conceptStats
            .filter { it.attempts > 0 }
            .map { stat ->
                val acc = (((stat.attempts - stat.errors).toDouble() / stat.attempts.toDouble()) * 100.0)
                    .toInt().coerceIn(0, 100)
                val confidence = ((acc * 0.7) + (stat.confidenceRating * 20.0 * 0.3)).toInt().coerceIn(5, 100)
                val rec = when {
                    stat.errors >= 2 && acc < 60 ->
                        "High-priority repair: Revise formula + solve 3 guided examples on '${stat.conceptTag}'."
                    acc < 80 ->
                        "Minor slip pattern detected (${stat.lastMistakeSummary.ifBlank { "check steps & units" }}). Do 2 timed drills."
                    else ->
                        "Solid grip on '${stat.conceptTag}' — keep light 10-min weekly revision."
                }
                ConceptWeaknessInsight(
                    conceptTag = stat.conceptTag,
                    chapterNumber = stat.chapterNumber,
                    attempts = stat.attempts,
                    errors = stat.errors,
                    accuracyPercent = acc,
                    confidenceScore = confidence,
                    recommendation = rec
                )
            }
            .sortedWith(compareByDescending<ConceptWeaknessInsight> { it.errors }.thenBy { it.accuracyPercent })
    }

    fun buildAutomaticStudyPlan(
        daysUntilExam: Int,
        dailyStudyMinutes: Int,
        masteryByChapter: Map<Int, MasteryStatus>,
        chapterProgressList: List<ChapterProgressEntity>,
        weaknesses: List<ConceptWeaknessInsight>
    ): List<StudyPlanSlot> {
        val safeDays = daysUntilExam.coerceIn(1, 365)
        val totalMinutes = dailyStudyMinutes.coerceIn(20, 300)
        val progressMap = chapterProgressList.associateBy { it.chapterNumber }
        val topWeaknessByChapter = weaknesses.groupBy { it.chapterNumber }

        // Exam-phase strategy adapts automatically as board exam approaches
        val revisionShare = when {
            safeDays <= 21 -> 0.55 // Final sprint: heavy revision & full papers
            safeDays <= 60 -> 0.35
            else -> 0.20
        }

        val scoredChapters = KsmkeSyllabusRepository.chapters.map { ch ->
            val st = masteryByChapter[ch.chapterNumber] ?: MasteryStatus.NOT_STARTED
            val acc = progressMap[ch.chapterNumber]?.accuracyPercent ?: when (st) {
                MasteryStatus.UNDERSTOOD -> 85
                MasteryStatus.PARTIALLY_UNDERSTOOD -> 60
                MasteryStatus.NEEDS_FOUNDATION -> 30
                MasteryStatus.NOT_STARTED -> 0
            }
            val chWeakness = topWeaknessByChapter[ch.chapterNumber]?.firstOrNull()
            val errorPenalty = (chWeakness?.errors ?: 0) * 15
            val masteryNeed = when (st) {
                MasteryStatus.NEEDS_FOUNDATION -> 45
                MasteryStatus.NOT_STARTED -> 35
                MasteryStatus.PARTIALLY_UNDERSTOOD -> 22
                MasteryStatus.UNDERSTOOD -> 8
            }
            val priorityScore = (ch.boardMarksWeightage * 4) + masteryNeed + errorPenalty + ((100 - acc) / 4)
            Triple(ch, priorityScore, chWeakness)
        }.sortedByDescending { it.second }.take(4)

        val totalPriority = scoredChapters.sumOf { it.second }.coerceAtLeast(1)
        var allocated = 0

        return scoredChapters.mapIndexed { idx, (ch, score, chWeakness) ->
            val mins = if (idx == scoredChapters.lastIndex) {
                (totalMinutes - allocated).coerceAtLeast(10)
            } else {
                ((totalMinutes * (score.toDouble() / totalPriority)).toInt() / 5 * 5).coerceIn(10, totalMinutes - 10)
            }
            allocated += mins

            val st = masteryByChapter[ch.chapterNumber] ?: MasteryStatus.NOT_STARTED
            val focus = when {
                chWeakness != null && chWeakness.errors > 0 -> "Weakness Repair"
                st == MasteryStatus.UNDERSTOOD || safeDays <= 21 -> "Timed Revision"
                st == MasteryStatus.NOT_STARTED -> "Concept Learning"
                else -> "Step Practice"
            }
            val targetConcept = chWeakness?.conceptTag ?: ch.detailedTopics.firstOrNull()?.topicTitleEn ?: ch.titleEn
            val revPct = (revisionShare * 100).toInt()
            val rationale = "$safeDays days left (${revPct}% revision mode) • Weightage ${ch.boardMarksWeightage}M • Status: ${st.label}"

            StudyPlanSlot(
                chapterNumber = ch.chapterNumber,
                chapterTitle = ch.titleEn,
                focusType = focus,
                dailyMinutes = mins,
                targetConcept = targetConcept,
                rationale = rationale
            )
        }
    }

    fun computeOverallConfidence(
        readinessScore: Int,
        weaknesses: List<ConceptWeaknessInsight>,
        verifiedTopicsCount: Int,
        totalTopicsCount: Int
    ): Int {
        val verificationPct = if (totalTopicsCount > 0) (verifiedTopicsCount * 100) / totalTopicsCount else 0
        val avgConceptConf = if (weaknesses.isNotEmpty()) weaknesses.map { it.confidenceScore }.average().toInt() else readinessScore
        return ((readinessScore * 0.45) + (verificationPct * 0.25) + (avgConceptConf * 0.30)).toInt().coerceIn(5, 99)
    }
}
