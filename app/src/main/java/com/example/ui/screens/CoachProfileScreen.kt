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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.CloudUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.SaffronGold

@Composable
fun CoachProfileScreen(
    viewModel: SslcStudyViewModel,
    studentName: String,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val cloudState by viewModel.cloudDashboard.collectAsStateWithLifecycle()
    val localGoals by viewModel.localGoals.collectAsStateWithLifecycle()
    val localExams by viewModel.localExamHistory.collectAsStateWithLifecycle()
    val localMastery by viewModel.localChapterProgress.collectAsStateWithLifecycle()

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

    // Personalized study timetable: prioritize NEEDS_FOUNDATION and NOT_STARTED high-weight chapters
    val masteryByCh = (cloudState as? CloudUiState.Success)?.data?.masteryByChapter
        ?.mapValues { it.value.safeStatus() }
        ?: localMastery.associate {
            it.chapterNumber to (enumValues<MasteryStatus>().firstOrNull { m -> m.name == it.masteryStatus }
                ?: MasteryStatus.NOT_STARTED)
        }

    val timetableChapters = KsmkeSyllabusRepository.chapters
        .sortedWith(
            compareBy<com.example.domain.KsmkeChapter> {
                when (masteryByCh[it.chapterNumber] ?: MasteryStatus.NOT_STARTED) {
                    MasteryStatus.NEEDS_FOUNDATION -> 0
                    MasteryStatus.NOT_STARTED -> 1
                    MasteryStatus.PARTIALLY_UNDERSTOOD -> 2
                    MasteryStatus.UNDERSTOOD -> 3
                }
            }.thenByDescending { it.boardMarksWeightage }
        )
        .take(5)

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
                                text = "Readiness: $readiness% • Goal: $targetScore/100 • Streak: $streakDays day(s)",
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
                        progress = { (readiness / 100f).coerceIn(0f, 1f) },
                        color = SaffronGold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Realistic Readiness Meter — builds steadily with verified chapter accuracy and timed tests.",
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

        // Personalized Study Timetable
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
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = IndigoInk)
                        Text("Personalised Weekly Priority Plan", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Automatically allocates extra practice time to chapters that need foundation while keeping mastered chapters light.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    timetableChapters.forEachIndexed { idx, ch ->
                        val st = masteryByCh[ch.chapterNumber] ?: MasteryStatus.NOT_STARTED
                        val slotMinutes = when (st) {
                            MasteryStatus.NEEDS_FOUNDATION -> 40
                            MasteryStatus.NOT_STARTED -> 30
                            MasteryStatus.PARTIALLY_UNDERSTOOD -> 20
                            MasteryStatus.UNDERSTOOD -> 10
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Slot ${idx + 1}: Ch ${ch.chapterNumber} • ${ch.titleEn}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${st.label} • Weightage ${ch.boardMarksWeightage}M",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = SaffronGold.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$slotMinutes min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (idx < timetableChapters.lastIndex) HorizontalDivider()
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
    }
}
