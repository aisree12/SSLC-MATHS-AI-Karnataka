package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.TargetedPaperQuestion
import com.example.ui.CloudUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.components.CameraSolutionScannerSheet
import com.example.ui.components.PdfSavedBanner
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.MathMonoTextStyle
import com.example.ui.theme.SaffronGold

@Composable
fun ChapterPaperWorkspace(
    viewModel: SslcStudyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val paper by viewModel.activeChapterPaper.collectAsStateWithLifecycle()
    val aiPaperGenState by viewModel.aiPaperGenState.collectAsStateWithLifecycle()
    val mcqAnswers by viewModel.paperMcqAnswers.collectAsStateWithLifecycle()
    val writtenAnswers by viewModel.paperWrittenAnswers.collectAsStateWithLifecycle()
    val qaChecks by viewModel.paperQaChecks.collectAsStateWithLifecycle()
    val pdfExportState by viewModel.pdfExportState.collectAsStateWithLifecycle()
    val cameraScanState by viewModel.cameraScanState.collectAsStateWithLifecycle()

    var scannerTargetQuestion by remember { mutableStateOf<TargetedPaperQuestion?>(null) }

    scannerTargetQuestion?.let { q ->
        CameraSolutionScannerSheet(
            scanState = cameraScanState,
            linkedQuestionText = "Q${q.questionNumber} (${q.marks}M): ${q.questionEn}",
            expectedAnswer = q.officialAnswer,
            initialUnableToSolve = false,
            onAnalyzeBitmap = { bmp, unable ->
                viewModel.analyzeCameraMathPhoto(
                    bitmap = bmp,
                    linkedQuestionText = q.questionEn,
                    expectedAnswer = q.officialAnswer,
                    studentUnableToSolve = unable
                )
            },
            onSaveToChat = { analysis ->
                viewModel.addScanAnalysisToChat("Ch ${paper.chapterNumber} Q${q.questionNumber}: ${q.questionEn}", analysis)
                viewModel.clearCameraScanState()
                scannerTargetQuestion = null
            },
            onDismiss = {
                viewModel.clearCameraScanState()
                scannerTargetQuestion = null
            }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = paper.paperTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = IndigoInk
                    )
                    Text(
                        text = "Ch ${paper.chapterNumber}: ${paper.chapterTitleEn} (${paper.chapterTitleKn}) • ${paper.totalMarks} Marks • ${paper.durationMinutes} Min",
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldChalk
                    )
                    Text(
                        text = paper.pyqBlueprintSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Chapter selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        KsmkeSyllabusRepository.chapters.forEach { ch ->
                            FilterChip(
                                selected = paper.chapterNumber == ch.chapterNumber,
                                onClick = { viewModel.selectPaperChapter(ch.chapterNumber) },
                                label = { Text("Ch ${ch.chapterNumber}") }
                            )
                        }
                    }

                    // AI Invent PYQ-Probability Paper CTA
                    Button(
                        onClick = { viewModel.generateAiProbabilityPaper(paper.chapterNumber) },
                        enabled = aiPaperGenState !is CloudUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_ai_chapter_paper_button")
                    ) {
                        if (aiPaperGenState is CloudUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("AI is inventing PYQ-probability paper...")
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Invent AI PYQ-Probability Paper (Ch ${paper.chapterNumber})")
                        }
                    }

                    when (val st = aiPaperGenState) {
                        is CloudUiState.Success -> Text(
                            text = st.data,
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldChalk
                        )
                        is CloudUiState.Error -> Text(
                            text = st.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        else -> Unit
                    }

                    // PDF Download Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.exportChapterPaperPdf(context, includeAnswerKey = false) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_paper_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Paper PDF")
                        }
                        Button(
                            onClick = { viewModel.exportChapterPaperPdf(context, includeAnswerKey = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldChalk),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_paper_with_key_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("PDF + Solutions")
                        }
                    }
                }
            }
        }

        if (pdfExportState != null) {
            item {
                PdfSavedBanner(
                    state = pdfExportState,
                    onDismiss = { viewModel.clearPdfExportState() }
                )
            }
        }

        items(paper.questions, key = { it.id }) { q ->
            ChapterPaperQuestionCard(
                question = q,
                selectedMcqIndex = mcqAnswers[q.id],
                writtenText = writtenAnswers[q.id].orEmpty(),
                qaCheckState = qaChecks[q.id],
                onSelectMcq = { idx -> viewModel.selectPaperMcqAnswer(q.id, idx) },
                onWrittenChange = { text -> viewModel.updatePaperWrittenAnswer(q.id, text) },
                onCheckQa = { viewModel.checkPaperQuestionAndAnswer(q, studentUnableToSolve = false) },
                onUnableToSolve = { viewModel.checkPaperQuestionAndAnswer(q, studentUnableToSolve = true) },
                onScanSolution = {
                    viewModel.clearCameraScanState()
                    scannerTargetQuestion = q
                }
            )
        }
    }
}

@Composable
private fun ChapterPaperQuestionCard(
    question: TargetedPaperQuestion,
    selectedMcqIndex: Int?,
    writtenText: String,
    qaCheckState: CloudUiState<String>?,
    onSelectMcq: (Int) -> Unit,
    onWrittenChange: (String) -> Unit,
    onCheckQa: () -> Unit,
    onUnableToSolve: () -> Unit,
    onScanSolution: () -> Unit
) {
    var showOfficialSolution by remember(question.id) { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = question.sectionLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = IndigoInk.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Q${question.questionNumber} • ${question.marks}M",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    color = SaffronGold.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = question.probabilityBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    color = EmeraldChalk.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (question.isAiInvented) "AI Targeted" else "PYQ Pattern",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldChalk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(text = question.questionEn, style = MaterialTheme.typography.bodyLarge)
            if (question.questionKn.isNotBlank()) {
                Text(
                    text = question.questionKn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = question.pyqTargetNote,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Interactive Student Solving Area
            if (question.mcqOptions.isNotEmpty()) {
                val labels = listOf("A", "B", "C", "D")
                question.mcqOptions.forEachIndexed { idx, opt ->
                    Surface(
                        onClick = { onSelectMcq(idx) },
                        color = if (selectedMcqIndex == idx) IndigoInk.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = selectedMcqIndex == idx,
                                onClick = { onSelectMcq(idx) }
                            )
                            Text(
                                text = "(${labels.getOrElse(idx) { "${idx + 1}" }}) $opt",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = writtenText,
                    onValueChange = onWrittenChange,
                    label = { Text("Solve here: Given -> Formula -> Steps -> Answer") },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solve_input_${question.id}")
                )
            }

            // Special Integrated Q & A Check + Camera Scan + Explain Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onCheckQa,
                    enabled = qaCheckState !is CloudUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoInk),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("check_qa_button_${question.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FactCheck,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Check Q & A")
                }

                OutlinedButton(
                    onClick = onScanSolution,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("scan_qa_button_${question.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Scan Work")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onUnableToSolve,
                    enabled = qaCheckState !is CloudUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Unable to Solve? Explain")
                }

                OutlinedButton(
                    onClick = { showOfficialSolution = !showOfficialSolution },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (showOfficialSolution) "Hide Key Steps" else "Key Steps")
                }
            }

            when (qaCheckState) {
                is CloudUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Text("Guruji is checking Question & Answer...", style = MaterialTheme.typography.bodySmall)
                    }
                }
                is CloudUiState.Error -> {
                    Text(
                        text = qaCheckState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is CloudUiState.Success -> {
                    Surface(
                        color = EmeraldChalk.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "Integrated Q & A Check Result",
                                style = MaterialTheme.typography.titleSmall,
                                color = EmeraldChalk
                            )
                            Text(
                                text = qaCheckState.data,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                null -> Unit
            }

            if (showOfficialSolution) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "Official Answer: ${question.officialAnswer}",
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldChalk
                        )
                        question.stepByStepSolution.forEach { st ->
                            Text(text = "• $st", style = MathMonoTextStyle)
                        }
                        Text(
                            text = "Avoid Mistake: ${question.commonMistake}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
