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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.KsmkeChapter
import com.example.domain.MasteryStatus
import com.example.domain.TopicDetailedNote
import com.example.ui.CloudUiState
import com.example.ui.SslcStudyViewModel
import com.example.ui.components.PdfSavedBanner
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.MathMonoTextStyle
import com.example.ui.theme.SaffronGold

@Composable
fun KsmkeSyllabusScreen(
    viewModel: SslcStudyViewModel,
    onAskGurujiForChapter: (Int) -> Unit,
    onOpenChapterPaper: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cloudState by viewModel.cloudDashboard.collectAsStateWithLifecycle()
    val localProgress by viewModel.localChapterProgress.collectAsStateWithLifecycle()
    val mathSolution by viewModel.mathSolution.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val aiChapterNotes by viewModel.aiChapterNotes.collectAsStateWithLifecycle()
    val pdfExportState by viewModel.pdfExportState.collectAsStateWithLifecycle()

    var expandedChapter by rememberSaveable { mutableIntStateOf(10) }
    var showMathLab by rememberSaveable { mutableStateOf(false) }

    val masteryMap: Map<Int, MasteryStatus> = when (val state = cloudState) {
        is CloudUiState.Success -> {
            val fromCloud = state.data.masteryByChapter.mapValues { it.value.safeStatus() }
            if (fromCloud.isNotEmpty()) fromCloud
            else localProgress.associate {
                it.chapterNumber to (enumValues<MasteryStatus>().firstOrNull { m -> m.name == it.masteryStatus }
                    ?: MasteryStatus.NOT_STARTED)
            }
        }
        else -> localProgress.associate {
            it.chapterNumber to (enumValues<MasteryStatus>().firstOrNull { m -> m.name == it.masteryStatus }
                ?: MasteryStatus.NOT_STARTED)
        }
    }

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
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "KSMKE • 6-Layer Knowledge Engine",
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "Complete 15 Karnataka SSLC Class 10 Chapters • Formulas, Theorems, Solved Steps, Marking Points & Math Verifier.",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color(0xFFE2E8F0)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showMathLab = !showMathLab },
                            modifier = Modifier.testTag("toggle_math_lab_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Calculate,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = if (showMathLab) "Hide Math Engine Lab" else "Open Math Verification Lab",
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }
        }

        syncMessage?.let { msg ->
            item {
                Surface(
                    color = EmeraldChalk.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.dismissSyncMessage() }
                ) {
                    Text(
                        text = "$msg (tap to dismiss)",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldChalk,
                        modifier = Modifier.padding(12.dp)
                    )
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

        if (showMathLab) {
            item {
                MathVerificationLabCard(
                    viewModel = viewModel,
                    solution = mathSolution
                )
            }
        }

        items(KsmkeSyllabusRepository.chapters, key = { it.chapterNumber }) { chapter ->
            val currentMastery = masteryMap[chapter.chapterNumber] ?: MasteryStatus.NOT_STARTED
            ChapterCard(
                chapter = chapter,
                mastery = currentMastery,
                expanded = expandedChapter == chapter.chapterNumber,
                aiNotesState = aiChapterNotes[chapter.chapterNumber],
                onToggleExpand = {
                    expandedChapter = if (expandedChapter == chapter.chapterNumber) -1 else chapter.chapterNumber
                },
                onUpdateMastery = { status ->
                    val acc = when (status) {
                        MasteryStatus.UNDERSTOOD -> 90
                        MasteryStatus.PARTIALLY_UNDERSTOOD -> 65
                        MasteryStatus.NEEDS_FOUNDATION -> 35
                        MasteryStatus.NOT_STARTED -> 0
                    }
                    viewModel.updateChapterMastery(
                        chapterNumber = chapter.chapterNumber,
                        status = status,
                        accuracyPercent = acc,
                        questionsSolved = chapter.solvedExamples.size * 5,
                        mistakeNote = chapter.commonMistakes.firstOrNull().orEmpty()
                    )
                },
                onDownloadNotesPdf = { viewModel.exportChapterNotesPdf(context, chapter.chapterNumber) },
                onExpandAiNotes = { viewModel.generateAiChapterNotes(chapter.chapterNumber) },
                onOpenChapterPaper = { onOpenChapterPaper(chapter.chapterNumber) },
                onAskGuruji = { onAskGurujiForChapter(chapter.chapterNumber) }
            )
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: KsmkeChapter,
    mastery: MasteryStatus,
    expanded: Boolean,
    aiNotesState: CloudUiState<String>?,
    onToggleExpand: () -> Unit,
    onUpdateMastery: (MasteryStatus) -> Unit,
    onDownloadNotesPdf: () -> Unit,
    onExpandAiNotes: () -> Unit,
    onOpenChapterPaper: () -> Unit,
    onAskGuruji: () -> Unit
) {
    val badgeColor = when (mastery) {
        MasteryStatus.UNDERSTOOD -> EmeraldChalk
        MasteryStatus.PARTIALLY_UNDERSTOOD -> SaffronGold
        MasteryStatus.NEEDS_FOUNDATION -> MaterialTheme.colorScheme.error
        MasteryStatus.NOT_STARTED -> MaterialTheme.colorScheme.outline
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_card_${chapter.chapterNumber}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = IndigoInk.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CH ${chapter.chapterNumber} • ${chapter.boardMarksWeightage}M",
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoInk,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = badgeColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = mastery.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = chapter.titleEn,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${chapter.titleKn} • ${chapter.textbookEdition} (${chapter.pageRange})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse chapter" else "Expand chapter"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    HorizontalDivider()
                    Text(text = chapter.summaryEn, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = chapter.summaryKn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(text = "Layer 2 • Essential Formulas", style = MaterialTheme.typography.titleSmall)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            chapter.keyFormulas.forEach { f ->
                                Text(text = "• $f", style = MathMonoTextStyle)
                            }
                        }
                    }

                    chapter.theorems.forEach { th ->
                        Text(
                            text = "Theorem (${th.marksWeight}M) • ${th.name} (${th.kannadaName})",
                            style = MaterialTheme.typography.titleSmall,
                            color = IndigoInk
                        )
                        Text(text = th.statement, style = MaterialTheme.typography.bodyMedium)
                        th.keyProofSteps.forEach { step ->
                            Text(text = step, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    chapter.solvedExamples.forEach { ex ->
                        Text(
                            text = "Solved Board Example • ${ex.title}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(text = ex.problem, style = MaterialTheme.typography.bodyMedium)
                        ex.markingSteps.forEach { st ->
                            Text(text = st, style = MathMonoTextStyle)
                        }
                        Text(
                            text = "Final Answer: ${ex.finalAnswer}",
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldChalk
                        )
                        Text(
                            text = "Examiner Tip: ${ex.examinerTip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SaffronGold
                        )
                    }

                    if (chapter.commonMistakes.isNotEmpty()) {
                        Text(text = "Common Exam Pitfalls", style = MaterialTheme.typography.titleSmall)
                        chapter.commonMistakes.forEach { m ->
                            Text(
                                text = "⚠ $m",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Text(text = "Self-Assess Your Mastery", style = MaterialTheme.typography.titleSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf(
                            MasteryStatus.NEEDS_FOUNDATION,
                            MasteryStatus.PARTIALLY_UNDERSTOOD,
                            MasteryStatus.UNDERSTOOD
                        ).forEach { st ->
                            FilterChip(
                                selected = mastery == st,
                                onClick = { onUpdateMastery(st) },
                                label = { Text(st.label) }
                            )
                        }
                    }

                    if (chapter.detailedTopics.isNotEmpty()) {
                        Text(
                            text = "Topic-Wise Detailed Notes (${chapter.detailedTopics.size} Topics • Complete Steps)",
                            style = MaterialTheme.typography.titleSmall,
                            color = IndigoInk
                        )
                        chapter.detailedTopics.forEach { topic ->
                            TopicDetailedNoteBlock(topic = topic)
                        }
                    }

                    // PDF Download & AI Deep Notes Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onDownloadNotesPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldChalk),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_notes_pdf_${chapter.chapterNumber}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Download Notes PDF")
                        }
                        OutlinedButton(
                            onClick = onExpandAiNotes,
                            enabled = aiNotesState !is CloudUiState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("expand_ai_notes_${chapter.chapterNumber}")
                        ) {
                            if (aiNotesState is CloudUiState.Loading) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Writing...")
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("AI Deep Notes")
                            }
                        }
                    }

                    when (aiNotesState) {
                        is CloudUiState.Error -> Text(
                            text = aiNotesState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is CloudUiState.Success -> {
                            Surface(
                                color = IndigoInk.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "AI Extended Topic-Wise Notes (Included in PDF)",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = IndigoInk
                                    )
                                    Text(text = aiNotesState.data, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        else -> Unit
                    }

                    OutlinedButton(
                        onClick = onOpenChapterPaper,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_chapter_paper_${chapter.chapterNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Solve Chapterwise PYQ-Probability Paper & Check Q & A")
                    }

                    Button(
                        onClick = onAskGuruji,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Teach Me This Chapter (10-Step Guruji)")
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicDetailedNoteBlock(topic: TopicDetailedNote) {
    var topicExpanded by rememberSaveable(topic.topicNumber) { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { topicExpanded = !topicExpanded }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Topic ${topic.topicNumber}: ${topic.topicTitleEn}",
                        style = MaterialTheme.typography.titleSmall,
                        color = IndigoInk
                    )
                    Text(
                        text = topic.topicTitleKn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (topicExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Toggle topic details"
                )
            }

            AnimatedVisibility(visible = topicExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    HorizontalDivider()
                    Text("Core Concepts & Definitions:", style = MaterialTheme.typography.labelLarge)
                    topic.coreConcepts.forEach { c ->
                        Text("• $c", style = MaterialTheme.typography.bodySmall)
                    }

                    if (topic.formulasAndRules.isNotEmpty()) {
                        Text("Formulas & Rules:", style = MaterialTheme.typography.labelLarge, color = EmeraldChalk)
                        topic.formulasAndRules.forEach { f ->
                            Text("• $f", style = MathMonoTextStyle)
                        }
                    }

                    if (topic.derivationsOrMethods.isNotEmpty()) {
                        Text("Method / Derivation Steps:", style = MaterialTheme.typography.labelLarge)
                        topic.derivationsOrMethods.forEach { d ->
                            Text(d, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    topic.workedExamples.forEach { ex ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Example: ${ex.title}", style = MaterialTheme.typography.labelLarge, color = IndigoInk)
                                Text("Q: ${ex.problem}", style = MaterialTheme.typography.bodySmall)
                                ex.markingSteps.forEach { s ->
                                    Text(s, style = MathMonoTextStyle)
                                }
                                Text("Answer: ${ex.finalAnswer}", style = MaterialTheme.typography.labelLarge, color = EmeraldChalk)
                                Text("Tip: ${ex.examinerTip}", style = MaterialTheme.typography.bodySmall, color = SaffronGold)
                            }
                        }
                    }

                    if (topic.examMistakesToAvoid.isNotEmpty()) {
                        Text("Mistakes to Avoid:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                        topic.examMistakesToAvoid.forEach { m ->
                            Text("⚠ $m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MathVerificationLabCard(
    viewModel: SslcStudyViewModel,
    solution: com.example.domain.MathEngineSolution?
) {
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0=Quadratic, 1=AP, 2=Linear Pair
    var p1 by rememberSaveable { mutableStateOf("2") }
    var p2 by rememberSaveable { mutableStateOf("-7") }
    var p3 by rememberSaveable { mutableStateOf("3") }
    var p4 by rememberSaveable { mutableStateOf("1") }
    var p5 by rememberSaveable { mutableStateOf("-1") }
    var p6 by rememberSaveable { mutableStateOf("-1") }

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
                text = "Deterministic Mathematics Verification Engine",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Independently computes exact algebraic roots, discriminants, AP terms, and linear pairs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Quadratic") })
                FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("AP (aₙ, Sₙ)") })
                FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("Linear Pair") })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = p1,
                    onValueChange = { p1 = it },
                    label = { Text(if (mode == 0) "a" else if (mode == 1) "a" else "a₁") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = p2,
                    onValueChange = { p2 = it },
                    label = { Text(if (mode == 0) "b" else if (mode == 1) "d" else "b₁") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = p3,
                    onValueChange = { p3 = it },
                    label = { Text(if (mode == 0) "c" else if (mode == 1) "n" else "c₁") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            if (mode == 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = p4,
                        onValueChange = { p4 = it },
                        label = { Text("a₂") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = p5,
                        onValueChange = { p5 = it },
                        label = { Text("b₂") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = p6,
                        onValueChange = { p6 = it },
                        label = { Text("c₂") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = {
                    val v1 = p1.toDoubleOrNull() ?: 1.0
                    val v2 = p2.toDoubleOrNull() ?: 0.0
                    val v3 = p3.toDoubleOrNull() ?: 0.0
                    when (mode) {
                        0 -> viewModel.runQuadraticVerifier(v1, v2, v3)
                        1 -> viewModel.runApVerifier(v1, v2, v3.toInt().coerceAtLeast(1))
                        else -> viewModel.runLinearPairVerifier(
                            v1, v2, v3,
                            p4.toDoubleOrNull() ?: 1.0,
                            p5.toDoubleOrNull() ?: 1.0,
                            p6.toDoubleOrNull() ?: 0.0
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verify_math_button")
            ) {
                Text("Verify Steps & Exact Solution")
            }

            solution?.let { sol ->
                Surface(
                    color = EmeraldChalk.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = sol.summary, style = MaterialTheme.typography.titleSmall, color = EmeraldChalk)
                        sol.steps.forEach { s -> Text(text = s, style = MathMonoTextStyle) }
                        Text(text = "Verified: ${sol.finalAnswer}", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}
