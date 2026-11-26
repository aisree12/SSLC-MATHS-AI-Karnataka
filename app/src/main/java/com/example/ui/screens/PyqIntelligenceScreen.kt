package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.ksmke.PyqAnalyticsEngine
import com.example.domain.PyqQuestion
import com.example.domain.QuestionSource
import com.example.ui.CloudUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.MathMonoTextStyle
import com.example.ui.theme.SaffronGold

@Composable
fun PyqIntelligenceScreen(
    viewModel: SslcStudyViewModel,
    onPracticeInGuruji: (PyqQuestion) -> Unit,
    modifier: Modifier = Modifier
) {
    val showChapterPapers by viewModel.pyqPaperTabSelected.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                FilterChip(
                    selected = !showChapterPapers,
                    onClick = { viewModel.setPyqPaperTabSelected(false) },
                    label = { Text("PYQ Bank (2015–2027)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_pyq_bank")
                )
                FilterChip(
                    selected = showChapterPapers,
                    onClick = { viewModel.setPyqPaperTabSelected(true) },
                    label = { Text("Chapterwise Papers & Q&A") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_chapter_papers")
                )
            }
        }

        if (showChapterPapers) {
            ChapterPaperWorkspace(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        } else {
            PyqBankContent(
                viewModel = viewModel,
                onPracticeInGuruji = onPracticeInGuruji,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PyqBankContent(
    viewModel: SslcStudyViewModel,
    onPracticeInGuruji: (PyqQuestion) -> Unit,
    modifier: Modifier = Modifier
) {
    val generatedPyqs by viewModel.generatedPyqs.collectAsStateWithLifecycle()
    val genState by viewModel.pyqGenState.collectAsStateWithLifecycle()

    var selectedMarks by rememberSaveable { mutableIntStateOf(0) } // 0 = All
    var selectedChapter by rememberSaveable { mutableIntStateOf(0) } // 0 = All
    var selectedSource by rememberSaveable { mutableStateOf<QuestionSource?>(null) }
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }

    val allQuestions = generatedPyqs + KsmkeSyllabusRepository.pyqQuestions
    val recurrenceStats = PyqAnalyticsEngine.computeRecurrenceStats(KsmkeSyllabusRepository.pyqQuestions)
        .filter { selectedChapter == 0 || it.chapterNumber == selectedChapter }
        .take(5)
    val officialCount = KsmkeSyllabusRepository.pyqQuestions.count {
        it.source == QuestionSource.HISTORICAL_BOARD || it.source == QuestionSource.OFFICIAL_MODEL_PAPER
    }

    val filtered = allQuestions.filter { q ->
        (selectedMarks == 0 || q.marks == selectedMarks) &&
            (selectedChapter == 0 || q.chapterNumber == selectedChapter) &&
            (selectedSource == null || q.source == selectedSource)
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SSLC PYQ Bank • 2015–2026 + 2026–27 Model Papers",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = ""$officialCount archived board/model-paper questions indexed. AI Practice questions are kept separate."",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Marks filter
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf(0 to "All Marks", 1 to "1M", 2 to "2M", 3 to "3M", 4 to "4M", 5 to "5M").forEach { (m, label) ->
                            FilterChip(
                                selected = selectedMarks == m,
                                onClick = { selectedMarks = m },
                                label = { Text(label) }
                            )
                        }
                    }

                    // Source filter
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = selectedSource == null,
                            onClick = { selectedSource = null },
                            label = { Text("All Sources") }
                        )
                        listOf(
                            QuestionSource.HISTORICAL_BOARD,
                            QuestionSource.OFFICIAL_MODEL_PAPER,
                            QuestionSource.AI_PRACTICE
                        ).forEach { src ->
                            FilterChip(
                                selected = selectedSource == src,
                                onClick = { selectedSource = src },
                                label = { Text(src.badge) }
                            )
                        }
                    }

                    // Chapter filter
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = selectedChapter == 0,
                            onClick = { selectedChapter = 0 },
                            label = { Text("All Chapters") }
                        )
                        KsmkeSyllabusRepository.chapters.forEach { ch ->
                            FilterChip(
                                selected = selectedChapter == ch.chapterNumber,
                                onClick = { selectedChapter = ch.chapterNumber },
                                label = { Text("Ch ${ch.chapterNumber}") }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val targetCh = if (selectedChapter == 0) 10 else selectedChapter
                            val targetMarks = if (selectedMarks == 0) 2 else selectedMarks.coerceAtMost(4)
                            viewModel.generateAiPracticeQuestions(targetCh, targetMarks)
                        },
                        enabled = genState !is CloudUiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_ai_practice_button")
                    ) {
                        if (genState is CloudUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Generate 3 New AI Practice Questions")
                        }
                    }

                    when (val st = genState) {
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
                }
            }
        }

        if (recurrenceStats.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndigoInk.copy(alpha = 0.06f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pyq_recurrence_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Archive Pattern Summary • Not an Exam Prediction",
                            style = MaterialTheme.typography.titleSmall,
                            color = IndigoInk
                        )
                        recurrenceStats.forEach { stat ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ch ${stat.chapterNumber}: ${stat.conceptTag}",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = "${stat.occurrences} questions • ${stat.distinctYears.size} archive years",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = EmeraldChalk.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${stat.totalMarksHistorically}M in archive",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldChalk,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        items(filtered, key = { it.id }) { q ->
            PyqItemCard(
                question = q,
                expanded = expandedId == q.id,
                onToggle = { expandedId = if (expandedId == q.id) null else q.id },
                onPractice = { onPracticeInGuruji(q) }
            )
        }
    }
}

@Composable
private fun PyqItemCard(
    question: PyqQuestion,
    expanded: Boolean,
    onToggle: () -> Unit,
    onPractice: () -> Unit
) {
    val srcColor = when (question.source) {
        QuestionSource.HISTORICAL_BOARD -> IndigoInk
        QuestionSource.OFFICIAL_MODEL_PAPER -> EmeraldChalk
        QuestionSource.AI_PRACTICE -> SaffronGold
        QuestionSource.TEXTBOOK_EXERCISE -> IndigoInk
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = srcColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${question.source.badge} • ${question.yearLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = srcColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${question.marks}M • Ch ${question.chapterNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Toggle key answer"
                )
            }

            Text(
                text = question.questionEn,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = question.questionKn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    HorizontalDivider()
                    Text(
                        text = "Official Answer: ${question.officialAnswer}",
                        style = MaterialTheme.typography.titleSmall,
                        color = EmeraldChalk
                    )
                    Text(
                        text = "Required Method: ${question.requiredMethod}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(text = "Board Marking Steps:", style = MaterialTheme.typography.titleSmall)
                    question.markingPoints.forEach { pt ->
                        Text(text = "• $pt", style = MathMonoTextStyle)
                    }
                    Text(
                        text = "Common Mistake: ${question.commonMistakes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = onPractice,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Solve & Discuss with Guruji")
                    }
                }
            }
        }
    }
}
