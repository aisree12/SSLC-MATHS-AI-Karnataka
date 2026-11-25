package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.VoiceTeacherHelper
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.ChatTurn
import com.example.domain.OpenWeightModelCatalog
import com.example.domain.OpenWeightProvider
import com.example.domain.TeachingLanguage
import com.example.ui.SslcStudyViewModel
import com.example.ui.components.CameraSolutionScannerSheet
import com.example.ui.theme.EmeraldChalk
import com.example.ui.theme.IndigoInk
import com.example.ui.theme.SaffronGold

@Composable
fun AiTeacherScreen(
    viewModel: SslcStudyViewModel,
    modifier: Modifier = Modifier
) {
    val chatTurns by viewModel.chatTurns.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val activeChapter by viewModel.activeChapter.collectAsStateWithLifecycle()
    val offlineMode by viewModel.offlineStudyMode.collectAsStateWithLifecycle()
    val isBusy by viewModel.isAiBusy.collectAsStateWithLifecycle()
    val aiError by viewModel.aiError.collectAsStateWithLifecycle()
    val cameraScanState by viewModel.cameraScanState.collectAsStateWithLifecycle()
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle()
    val selectedModelId by viewModel.selectedModelId.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }
    var showScannerSheet by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var voiceStatus by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val voiceHelper = remember(context) {
        VoiceTeacherHelper(
            context = context,
            onListeningChanged = { isListening = it },
            onSpeechResult = { spoken ->
                voiceStatus = null
                input = spoken
            },
            onVoiceStatus = { msg -> voiceStatus = msg },
            onSpeakingChanged = { isSpeaking = it }
        )
    }

    DisposableEffect(voiceHelper) {
        onDispose { voiceHelper.release() }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceStatus = "Listening in ${if (language == TeachingLanguage.KN) "Kannada" else "English"}..."
            voiceHelper.startListening(language)
        } else {
            voiceStatus = "Microphone permission denied. You can still type your question or scan a notebook page."
        }
    }

    if (showScannerSheet) {
        CameraSolutionScannerSheet(
            scanState = cameraScanState,
            onAnalyzeBitmap = { bmp, unableToSolve ->
                viewModel.analyzeCameraMathPhoto(
                    bitmap = bmp,
                    studentUnableToSolve = unableToSolve
                )
            },
            onSaveToChat = { analysis ->
                viewModel.addScanAnalysisToChat("Camera math scan", analysis)
                viewModel.clearCameraScanState()
                showScannerSheet = false
            },
            onDismiss = {
                viewModel.clearCameraScanState()
                showScannerSheet = false
            }
        )
    }

    LaunchedEffect(chatTurns.size, chatTurns.lastOrNull()?.text?.length) {
        if (chatTurns.isNotEmpty()) {
            listState.animateScrollToItem(chatTurns.lastIndex)
        }
    }

    val selectedChapter = activeChapter?.let { KsmkeSyllabusRepository.getChapter(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Header & Language + Offline Study Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Guruji • SSLC AI Teacher",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = selectedChapter?.let { "Ch ${it.chapterNumber}: ${it.titleEn} (${it.titleKn})" }
                                ?: "All 15 Karnataka SSLC Chapters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = "Offline Study Mode",
                            tint = if (offlineMode) SaffronGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Switch(
                            checked = offlineMode,
                            onCheckedChange = { viewModel.setOfflineStudyMode(it) },
                            modifier = Modifier.testTag("offline_mode_switch")
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    TeachingLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = language == lang,
                            onClick = { viewModel.setLanguage(lang) },
                            label = { Text(lang.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Quick chapter selector chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    KsmkeSyllabusRepository.chapters.forEach { ch ->
                        FilterChip(
                            selected = activeChapter == ch.chapterNumber,
                            onClick = { viewModel.setActiveChapter(ch.chapterNumber) },
                            label = { Text("Ch ${ch.chapterNumber}: ${ch.titleEn}") }
                        )
                    }
                }

                // Free Open-Weight OpenAI-Compatible Provider & Model Picker (Qwen, DeepSeek, Llama, Mistral, Ollama)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    OpenWeightProvider.entries.forEach { provider ->
                        FilterChip(
                            selected = selectedProvider == provider,
                            onClick = { viewModel.selectOpenWeightProvider(provider) },
                            label = { Text(provider.displayName) }
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    OpenWeightModelCatalog.modelsFor(selectedProvider).forEach { model ->
                        FilterChip(
                            selected = selectedModelId == model.id,
                            onClick = { viewModel.selectOpenWeightModel(model.id) },
                            label = { Text(model.label) }
                        )
                    }
                }
                if (selectedProvider.requiresApiKey && !viewModel.isOpenAiKeyConfigured) {
                    Text(
                        text = "Tip: Add a free ${selectedProvider.displayName} key to OPENAI_COMPAT_API_KEY in AI Studio Secrets, or switch to Local Ollama / Offline Mode.",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold
                    )
                }
            }
        }

        // Starter Prompts
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            val prompts = listOf(
                "Why do we use the quadratic formula?",
                "I cannot understand Trigonometry. Start from zero.",
                "Solve 2x² - 7x + 3 = 0 step by step",
                "Explain Thales Theorem for 4 marks",
                "ಕನ್ನಡದಲ್ಲಿ ವರ್ಗ ಸಮೀಕರಣವನ್ನು ವಿವರಿಸಿ"
            )
            prompts.forEach { sample ->
                AssistChip(
                    onClick = { viewModel.askTeacher(sample) },
                    label = { Text(sample, maxLines = 1) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        // Chat Transcript
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatTurns, key = { it.id }) { turn ->
                ChatBubble(
                    turn = turn,
                    isSpeaking = isSpeaking,
                    onSpeakToggle = { textToSpeak ->
                        voiceStatus = null
                        if (isSpeaking) voiceHelper.stopSpeaking()
                        else voiceHelper.speakExplanation(textToSpeak, language)
                    }
                )
            }
        }

        voiceStatus?.let { status ->
            Surface(
                color = SaffronGold.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = IndigoInk,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Error Banner with Offline KSMKE Fallback
        aiError?.let { err ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.useOfflineFallbackForLastQuestion() }) {
                            Text("Use Offline KSMKE Guide")
                        }
                        OutlinedButton(onClick = { viewModel.clearAiError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }

        // Input Composer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.clearCameraScanState()
                    showScannerSheet = true
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = EmeraldChalk.copy(alpha = 0.16f),
                    contentColor = EmeraldChalk
                ),
                modifier = Modifier
                    .size(50.dp)
                    .testTag("camera_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Scan notebook solution or question with camera"
                )
            }
            IconButton(
                onClick = {
                    if (isListening) {
                        voiceHelper.stopListening()
                    } else {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                            voiceStatus = "Listening in ${if (language == TeachingLanguage.KN) "Kannada" else "English"}..."
                            voiceHelper.startListening(language)
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isListening) MaterialTheme.colorScheme.errorContainer else SaffronGold.copy(alpha = 0.18f),
                    contentColor = if (isListening) MaterialTheme.colorScheme.error else IndigoInk
                ),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("voice_mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                    contentDescription = if (isListening) "Stop voice recording" else "Speak question in Kannada or English"
                )
            }
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = {
                    Text(
                        if (offlineMode) "Ask offline KSMKE / speak..."
                        else "Ask Guruji, speak or scan..."
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("teacher_question_input")
            )
            IconButton(
                onClick = {
                    val toSend = input
                    input = ""
                    viewModel.askTeacher(toSend)
                },
                enabled = input.isNotBlank() && !isBusy,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = IndigoInk,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(50.dp)
                    .testTag("send_teacher_button")
            ) {
                if (isBusy) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send question"
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    turn: ChatTurn,
    isSpeaking: Boolean,
    onSpeakToggle: (String) -> Unit
) {
    Row(
        horizontalArrangement = if (turn.isStudent) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (turn.isStudent) IndigoInk else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (turn.isStudent) 18.dp else 4.dp,
                bottomEnd = if (turn.isStudent) 4.dp else 18.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (turn.isStudent) 0.dp else 2.dp),
            modifier = Modifier.widthIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!turn.isStudent) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (turn.isStreaming) "Guruji is writing..." else "Guruji • KSMKE Teacher",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGold
                        )
                        if (turn.text.isNotBlank() && !turn.isStreaming) {
                            IconButton(
                                onClick = { onSpeakToggle(turn.text) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Read explanation aloud",
                                    tint = IndigoInk,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (turn.text.isEmpty() && turn.isStreaming) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                } else {
                    Text(
                        text = turn.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (turn.isStudent) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                turn.mathVerificationNote?.let { note ->
                    Surface(
                        color = EmeraldChalk.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified by Mathematics Engine",
                                tint = EmeraldChalk,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = note,
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldChalk
                            )
                        }
                    }
                }
            }
        }
    }
}
