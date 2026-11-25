package com.example.data.ksmke

import com.example.domain.PyqQuestion
import com.example.domain.PyqRecurrenceStat
import com.example.domain.QuestionSource

/**
 * Computes actual historical recurrence statistics directly from the
 * Official 2015–2027 KSEAB PYQ & Model Paper archive (never hardcoded guesses).
 */
object PyqAnalyticsEngine {

    fun computeRecurrenceStats(allQuestions: List<PyqQuestion>): List<PyqRecurrenceStat> {
        val officialOnly = allQuestions.filter {
            it.source == QuestionSource.HISTORICAL_BOARD || it.source == QuestionSource.OFFICIAL_MODEL_PAPER
        }
        val totalCycles = officialOnly.map { it.yearLabel }.distinct().size.coerceAtLeast(1)

        return officialOnly
            .groupBy { q -> q.conceptTag.ifBlank { q.topic } to q.chapterNumber }
            .map { (key, list) ->
                val (concept, chNum) = key
                val years = list.map { it.yearLabel }.distinct().sorted()
                val pct = ((years.size.toDouble() / totalCycles.toDouble()) * 100.0)
                    .toInt().coerceIn(8, 100)
                val label = when {
                    pct >= 30 || list.size >= 3 -> "High Recurrence ($pct% cycles)"
                    list.size == 2 -> "Recurring Pattern ($pct% cycles)"
                    else -> "Cycle Sample ($pct% cycles)"
                }
                PyqRecurrenceStat(
                    conceptTag = concept,
                    chapterNumber = chNum,
                    occurrences = list.size,
                    distinctYears = years,
                    totalMarksHistorically = list.sumOf { it.marks },
                    recurrencePercent = pct,
                    yieldLabel = label
                )
            }
            .sortedWith(
                compareByDescending<PyqRecurrenceStat> { it.occurrences }
                    .thenByDescending { it.totalMarksHistorically }
            )
    }

    fun computeChapterArchiveSummary(allQuestions: List<PyqQuestion>): Map<Int, Pair<Int, Int>> {
        val officialOnly = allQuestions.filter {
            it.source == QuestionSource.HISTORICAL_BOARD || it.source == QuestionSource.OFFICIAL_MODEL_PAPER
        }
        return officialOnly.groupBy { it.chapterNumber }
            .mapValues { (_, qs) -> qs.size to qs.sumOf { it.marks } }
    }
}
