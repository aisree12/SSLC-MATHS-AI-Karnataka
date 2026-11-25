package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.signOut
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.MasteryStatus
import com.example.domain.TopicCompletionStatus
import com.example.domain.WeaknessAndPlannerEngine
import com.example.ui.CloudUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.SaffronGold
import kotlin.math.roundToInt

@Composable
fun CoachProfileScreen(
    viewModel: SslcStudyViewModel,
    studentName: String,
    onSignedOut: () -> Unit,
    onReplayIntro: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val cloudState by viewModel.cloudDashboard.collectAsStateWithLifecycle()
    val localGoals by viewModel.localGoals.collectAsStateWithLifecycle()
    val localExams by viewModel.localExamHistory.collectAsStateWithLifecycle()
    val localMastery by viewModel.localChapterProgress.collectAsStateWithLifecycle()
    val conceptWeaknesses by viewModel.conceptWeaknesses.collectAsStateWithLifecycle()
    val daysUntilExam by viewModel.daysUntilExam.collectAsStateWithLifecycle()
    val dailyStudyMinutes by viewModel.dailyStudyMinutes.collectAsStateWithLifecycle()
    val topicVerifications by viewModel.topicVerifications.collectAsStateWithLifecycle()

    var newGoalTitle by rememberSaveable { mutableStateOf("") }
    var calmTipIndex by rememberSaveable { mutableStateOf(0) }

    val calmTips = listOf(
        "Anxiety Tip: You do not need to learn everything today. Pick just one 15-minute topic and finish 2 clean examples.",
        "Mistake Mindset: Getting Step 2 wrong means Step 1 already works! Every fixed mistake saves 1 real board mark.",
        "4-4-4 Exam Breathing: Before reading the question paper, breathe in 4s, hold 4s, breathe out 4s. Your calm mind recalls formulas twice as fast."
    )

    val profile = (cloudState as? CloudUiState.Success)?.data?.profile
    val readiness = profile?.readinessScore?.toInt() ?: 52
    val targetScore = profile?.targetScore?.toInt() ?: 90
    val streakDays = profile?.streakDays?.toInt() ?: 1

    val masteryByCh = (cloudState as? CloudUiState.Success)?.data?.masteryByChapter
        ?.mapValues { it.value.safeStatus() }
        ?: localMastery.associate {
            it.chapterNumber to (enumValues<MasteryStatus>().firstOrNull { m -> m.name == it.masteryStatus }
                ?: MasteryStatus.NOT_STARTED)
        }

    val totalTopicsCount = KsmkeSyllabusRepository.chapters.sumOf { it.detailedTopics.size }
    val verifiedTopicsCount = topicVerifications.values.count { it == TopicCompletionStatus.VERIFIED_COMPLETE }
    val overallConfidence = WeaknessAndPlannerEngine.computeOverallConfidence(
        readinessScore = readiness,
        weaknesses = conceptWeaknesses,
        verifiedTopicsCount = verifiedTopicsCount,
        totalTopicsCount = totalTopicsCount
    )
    val automaticPlan = WeaknessAndPlannerEngine.buildAutomaticStudyPlan(
        daysUntilExam = daysUntilExam,
        dailyStudyMinutes = dailyStudyMinutes,
        masteryByChapter = masteryByCh,
        chapterProgressList = localMastery,
        weaknesses = conceptWeaknesses
    )

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndigoInk),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = studentName.ifBlank { "SSLC Scholar" },
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Readiness: $readiness% • Confidence: $overallConfidence% • Goal: $targetScore/100",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                signOut(credentialManager, scope, onSignOutComplete = onSignedOut)
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign out",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("Sign Out", color = Color.White)
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (overallConfidence / 100f).coerceIn(0f, 1f) },
                        color = SaffronGold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Personalised Learning-Confidence Index ($overallConfidence%) • Verified $verifiedTopicsCount/$totalTopicsCount topics • Streak: $streakDays day(s).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        if (cloudState is CloudUiState.Error) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cloud sync notice: ${(cloudState as CloudUiState.Error).message}. Local Room progress remains active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Psychological & Confidence Coach
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Psychology, contentDescription = null, tint = EmeraldChalk)
                        Text("Confidence & Exam Stress Coach", style = MaterialTheme.typography.titleMedium)
                    }
                    Surface(
                        color = EmeraldChalk.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = calmTips[calmTipIndex % calmTips.size],
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = { calmTipIndex = (calmTipIndex + 1) % calmTips.size },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Next Confidence Reminder")
                    }
                }
            }
        }

        // Intelligent Concept Weakness Detection & Confidence Tracker
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("concept_weakness_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.TrackChanges, contentDescription = null, tint = SaffronGold)
                        Text("Intelligent Concept Weakness & Confidence", style = MaterialTheme.typography.titleMedium)
                    }
                    if (conceptWeaknesses.isEmpty()) {
                        Text(
                            text = "Solve questions in Answer-Writing Coach or Exam Simulator to detect exact concept errors (instead of blaming a whole chapter).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        conceptWeaknesses.take(4).forEachIndexed { idx, w ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Ch ${w.chapterNumber} • ${w.conceptTag}",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${w.accuracyPercent}% acc • ${w.errors}/${w.attempts} errors",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (w.accuracyPercent < 70) MaterialTheme.colorScheme.error else EmeraldChalk
                                    )
                                }
                                Text(
                                    text = w.recommendation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(1 to "Low", 3 to "Okay", 5 to "Confident").forEach { (r, label) ->
                                        FilterChip(
                                            selected = false,
                                            onClick = { viewModel.setConceptConfidenceRating(w.conceptTag, w.chapterNumber, r) },
                                            label = { Text(label) }
                                        )
                                    }
                                }
                            }
                            if (idx < conceptWeaknesses.take(4).lastIndex) HorizontalDivider()
                        }
                    }
                }
            }
        }

        // Automatic Exam-Countdown Study Planner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("automatic_study_planner_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = IndigoInk)
                        Text("Automatic Exam-Countdown Planner", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Days before SSLC Maths Board: $daysUntilExam days",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = daysUntilExam.toFloat(),
                        onValueChange = { viewModel.updateStudyPlannerConfig(it.roundToInt(), dailyStudyMinutes) },
                        valueRange = 5f..180f
                    )
                    Text(
                        text = "Daily Study Budget: $dailyStudyMinutes min/day",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = dailyStudyMinutes.toFloat(),
                        onValueChange = { viewModel.updateStudyPlannerConfig(daysUntilExam, (it / 10f).roundToInt() * 10) },
                        valueRange = 30f..240f
                    )
                    HorizontalDivider()
                    automaticPlan.forEachIndexed { idx, slot ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${idx + 1}. Ch ${slot.chapterNumber} • ${slot.chapterTitle} (${slot.focusType})",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Concept: ${slot.targetConcept}\n${slot.rationale}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = SaffronGold.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${slot.dailyMinutes}m/d",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (idx < automaticPlan.lastIndex) HorizontalDivider()
                    }
                }
            }
        }

        // Daily Achievable Goals
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Daily Achievable Goals", style = MaterialTheme.typography.titleMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newGoalTitle,
                            onValueChange = { newGoalTitle = it },
                            placeholder = { Text("Add a small daily target...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newGoalTitle.isNotBlank()) {
                                    viewModel.addPersonalizedGoal(
                                        chapterNumber = viewModel.activeChapter.value ?: 10,
                                        title = newGoalTitle,
                                        targetMinutes = 20
                                    )
                                    newGoalTitle = ""
                                }
                            },
                            modifier = Modifier.testTag("add_goal_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add goal")
                        }
                    }
                }
            }
        }

        items(localGoals, key = { it.goalId }) { goal ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleDailyGoal(goal) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = goal.completed,
                        onCheckedChange = { viewModel.toggleDailyGoal(goal) }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = goal.title, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${goal.scheduledSlot} • ${goal.targetMinutes} min • Ch ${goal.chapterNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (localExams.isNotEmpty()) {
            item {
                Text("Recent Mock Exam History", style = MaterialTheme.typography.titleMedium)
            }
            items(localExams.take(5), key = { it.id }) { exam ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${exam.examTitle}: ${exam.scoreObtained}/${exam.maxMarks}",
                            style = MaterialTheme.typography.titleSmall,
                            color = IndigoInk
                        )
                        Text(
                            text = exam.feedbackSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Developer & App Credits Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndigoInk),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_developer_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = "Developer",
                            tint = SaffronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Developer: SVANAM",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Phone: 6360953674 • City: VIJAYAPUR",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                    OutlinedButton(
                        onClick = onReplayIntro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("replay_intro_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Replay Colorful Intro", color = Color.White)
                    }
                }
            }
        }
    }
}
