package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.ExamMode
import com.example.domain.MathVerificationEngine
import com.example.ui.CloudUiState
import com.example.ui.ExamSessionUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.IndigoInkDeep
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MathMonoTextStyle
import com.example.ui.theme.SaffronGold

@Composable
fun FullMarksAcademyScreen(
    viewModel: SslcStudyViewModel,
    modifier: Modifier = Modifier
) {
    val examState by viewModel.examState.collectAsStateWithLifecycle()
    val answerEvalState by viewModel.answerEvalState.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Exam Simulator, 1 = Answer Writing Coach

    if (examState.active) {
        ActiveExamSimulatorView(
            state = examState,
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_exam_academy),
                    contentDescription = "SSLC Full Marks Academy",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(IndigoInkDeep.copy(alpha = 0.88f), IndigoInkDeep.copy(alpha = 0.35f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "SSLC FULL MARKS ACADEMY",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGold
                        )
                        Text(
                            text = "Learn • Practise • Present • Perfect",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Marks Maximisation Engine & 80-Mark Clock Training",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        item {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Exam Simulator") },
                    icon = { Icon(Icons.Filled.Timer, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Answer-Writing Coach") },
                    icon = { Icon(Icons.Filled.EditNote, contentDescription = null) }
                )
            }
        }

        if (selectedTab == 0) {
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
                        Text(
                            text = "Three-Phase Board Exam Clock",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Illustrative training pace for the 80-mark, 38-question, 195-minute SSLC paper (confirm final instructions against the official board paper):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("• First 15 min: Read & Plan question selection", style = MaterialTheme.typography.bodyMedium)
                        Text("• Next 150 min: Solve step-by-step with neat figures", style = MaterialTheme.typography.bodyMedium)
                        Text("• Last 30 min: Review signs, units & unanswered items", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            ExamMode.entries.forEach { mode ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = mode.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = mode.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { viewModel.startExamSimulator(mode) },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoInk),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_exam_${mode.name}")
                            ) {
                                Text("Start ${mode.maxMarks}-Mark Timed Test")
                            }
                        }
                    }
                }
            }
        } else {
            item {
                AnswerWritingCoachCard(
                    evalState = answerEvalState,
                    onEvaluate = { qText, expected, marks, steps ->
                        viewModel.evaluateStudentAnswer(qText, expected, marks, steps)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveExamSimulatorView(
    state: ExamSessionUiState,
    viewModel: SslcStudyViewModel,
    modifier: Modifier = Modifier
) {
    if (state.finished) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
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
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Marks Maximisation Report",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = state.reportSummary,
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaffronGold
                        )
                    }
                }
            }

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
                        Text(
                            text = "Time Management & Speed Diagnostic",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.slowestDiagnostic,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider()
                        Text(
                            text = "Questions Skipped: ${state.skippedIndices.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Total Time Used: ${state.elapsedSeconds / 60}m ${state.elapsedSeconds % 60}s",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            onClick = { viewModel.exitExamSimulator() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to Full Marks Academy")
                        }
                    }
                }
            }
        }
        return
    }

    val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
    val remaining = (state.totalSeconds - state.elapsedSeconds).coerceAtLeast(0)
    val mins = remaining / 60
    val secs = remaining % 60
    val qTime = state.questionSeconds[state.currentIndex] ?: 0
    val isSlow = qTime > currentQ.format.recommendedMinutes * 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Exam Clock Bar
        Surface(
            color = IndigoInk,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.currentPhase.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGold
                    )
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontFamily = JetBrainsMonoFontFamily,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Text(
                    text = state.currentPhase.guidance,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0)
                )
                LinearProgressIndicator(
                    progress = { (state.elapsedSeconds.toFloat() / state.totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    color = SaffronGold,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Question Palette Strip
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            state.questions.forEachIndexed { idx, _ ->
                val answered = state.selectedMcqAnswers.containsKey(idx) ||
                    !state.writtenAnswers[idx].isNullOrBlank()
                FilterChip(
                    selected = state.currentIndex == idx,
                    onClick = { viewModel.selectExamQuestion(idx) },
                    label = { Text(if (answered) "Q${idx + 1} ✓" else "Q${idx + 1}") }
                )
            }
        }

        // Active Question Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Q${state.currentIndex + 1} of ${state.questions.size} • ${currentQ.marks} Marks",
                            style = MaterialTheme.typography.titleSmall,
                            color = IndigoInk
                        )
                        Text(
                            text = "This Q: ${qTime}s (Target ≤ ${currentQ.format.recommendedMinutes}m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSlow) MaterialTheme.colorScheme.error else EmeraldChalk
                        )
                    }
                }
                item {
                    Text(text = currentQ.questionEn, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = currentQ.questionKn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (currentQ.mcqOptions.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            currentQ.mcqOptions.forEachIndexed { optIdx, optText ->
                                val selected = state.selectedMcqAnswers[state.currentIndex] == optIdx
                                Surface(
                                    color = if (selected) IndigoInk.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.answerMcqInExam(state.currentIndex, optIdx) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        RadioButton(
                                            selected = selected,
                                            onClick = { viewModel.answerMcqInExam(state.currentIndex, optIdx) }
                                        )
                                        Text(text = optText, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        OutlinedTextField(
                            value = state.writtenAnswers[state.currentIndex].orEmpty(),
                            onValueChange = { viewModel.updateWrittenAnswerInExam(state.currentIndex, it) },
                            label = { Text("Write Given -> Formula -> Steps -> Answer") },
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text(
                            text = "Self-Evaluation Marking Key (for Step Practice):",
                            style = MaterialTheme.typography.labelMedium
                        )
                        currentQ.markingPoints.forEach { pt ->
                            Text(text = "• $pt", style = MathMonoTextStyle)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf(0.0, currentQ.marks * 0.5, currentQ.marks.toDouble()).forEach { m ->
                                FilterChip(
                                    selected = state.selfAssessedMarks[state.currentIndex] == m,
                                    onClick = { viewModel.setSelfAssessedMarksInExam(state.currentIndex, m) },
                                    label = { Text("Award ${MathVerificationEngine.fmt(m)}M") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { viewModel.toggleSkipExamQuestion(state.currentIndex) },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (state.currentIndex in state.skippedIndices) "Unskip" else "Skip for Review")
            }
            if (state.currentIndex < state.questions.lastIndex) {
                Button(
                    onClick = { viewModel.selectExamQuestion(state.currentIndex + 1) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next Question")
                }
            } else {
                Button(
                    onClick = { viewModel.finishExamSimulator() },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldChalk),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("submit_exam_button")
                ) {
                    Text("Submit Exam")
                }
            }
        }
    }
}

@Composable
private fun AnswerWritingCoachCard(
    evalState: CloudUiState<String>?,
    onEvaluate: (String, String, Int, String) -> Unit
) {
    val sampleQuestions = KsmkeSyllabusRepository.pyqQuestions.filter { it.marks in 2..3 }
    var selectedIdx by rememberSaveable { mutableIntStateOf(0) }
    val activeQ = sampleQuestions[selectedIdx % sampleQuestions.size]
    var studentSteps by rememberSaveable {
        mutableStateOf(
            "Given: a = 2, d = 7 - 2 = 5, n = 15\nFormula: an = a + (n - 1)d\nSubstitution: a15 = 2 + (15 - 1)(5) = 2 + 70\nFinal Answer: a15 = 72"
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "5-Step Board Answer-Writing Trainer",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Examiners award marks for: 1) Given  2) Formula  3) Substitution  4) Step Calculation  5) Highlighted Answer + Units.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                sampleQuestions.take(5).forEachIndexed { idx, q ->
                    FilterChip(
                        selected = selectedIdx == idx,
                        onClick = { selectedIdx = idx },
                        label = { Text("${q.marks}M • Ch ${q.chapterNumber}") }
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = activeQ.questionEn,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            OutlinedTextField(
                value = studentSteps,
                onValueChange = { studentSteps = it },
                label = { Text("Your Step-by-Step Board Presentation") },
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("answer_coach_input")
            )

            Button(
                onClick = {
                    onEvaluate(
                        activeQ.questionEn,
                        activeQ.officialAnswer,
                        activeQ.marks,
                        studentSteps
                    )
                },
                enabled = evalState !is CloudUiState.Loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evaluate_steps_button")
            ) {
                if (evalState is CloudUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Check Marking Points with AI Examiner")
                }
            }

            when (evalState) {
                is CloudUiState.Success -> {
                    Surface(
                        color = EmeraldChalk.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = evalState.data,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                is CloudUiState.Error -> {
                    Text(
                        text = evalState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}
