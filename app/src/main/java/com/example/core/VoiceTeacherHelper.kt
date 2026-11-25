package com.example.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.domain.TeachingLanguage
import java.util.Locale

/**
 * Voice-Based Personal Teacher helper wrapping Android TextToSpeech and SpeechRecognizer
 * for English (en-IN) and Kannada (kn-IN) with graceful offline/emulator availability checks.
 */
class VoiceTeacherHelper(
    context: Context,
    private val onListeningChanged: (Boolean) -> Unit,
    private val onSpeechResult: (String) -> Unit,
    private val onVoiceStatus: (String) -> Unit,
    private val onSpeakingChanged: (Boolean) -> Unit
) {
    private val appContext = context.applicationContext
    private var ttsReady = false
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        tts = TextToSpeech(appContext) { status ->
            ttsReady = (status == TextToSpeech.SUCCESS)
            if (ttsReady) {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onSpeakingChanged(true)
                    }

                    override fun onDone(utteranceId: String?) {
                        onSpeakingChanged(false)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onSpeakingChanged(false)
                    }
                })
            }
        }
    }

    fun isSpeechRecognitionAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(appContext)

    fun speakExplanation(rawText: String, language: TeachingLanguage) {
        val engine = tts
        if (engine == null || !ttsReady) {
            onVoiceStatus("Voice synthesis engine is initialising or unavailable on this device.")
            return
        }

        val targetLocale = when (language) {
            TeachingLanguage.KN -> Locale("kn", "IN")
            TeachingLanguage.EN, TeachingLanguage.BILINGUAL -> Locale("en", "IN")
        }
        val langResult = engine.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            if (language == TeachingLanguage.KN) {
                val fallbackRes = engine.setLanguage(Locale("en", "IN"))
                if (fallbackRes == TextToSpeech.LANG_MISSING_DATA || fallbackRes == TextToSpeech.LANG_NOT_SUPPORTED) {
                    onVoiceStatus("Kannada/English offline TTS voice pack is not installed on this device.")
                    return
                }
                onVoiceStatus("Kannada offline voice pack missing; reading in Indian English.")
            } else {
                engine.setLanguage(Locale.US)
            }
        }

        val spoken = normalizeMathForSpeech(rawText).take(3500)
        engine.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "guruji_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        onSpeakingChanged(false)
    }

    fun startListening(language: TeachingLanguage) {
        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
            onVoiceStatus("Speech recognition service is not installed on this device/emulator. Type your question below.")
            onListeningChanged(false)
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onListeningChanged(true)
                }

                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() {
                    onListeningChanged(false)
                }

                override fun onError(error: Int) {
                    onListeningChanged(false)
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "Could not hear clearly. Try speaking closer to the mic or type below."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "Offline speech pack unavailable for this language."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording unavailable."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required for voice input."
                        else -> "Voice input paused (code $error). You can type anytime."
                    }
                    onVoiceStatus(msg)
                }

                override fun onResults(results: Bundle?) {
                    onListeningChanged(false)
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.trim()
                        .orEmpty()
                    if (text.isNotBlank()) {
                        onSpeechResult(text)
                    } else {
                        onVoiceStatus("No speech detected. Try again.")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })
        }

        val langTag = if (language == TeachingLanguage.KN) "kn-IN" else "en-IN"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Guruji in English or Kannada")
        }
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            onListeningChanged(false)
            onVoiceStatus(e.localizedMessage ?: "Unable to start microphone.")
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        onListeningChanged(false)
    }

    fun release() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private fun normalizeMathForSpeech(input: String): String {
        return input
            .replace("x²", "x squared")
            .replace("a²", "a squared")
            .replace("b²", "b squared")
            .replace("r²", "r squared")
            .replace("r³", "r cubed")
            .replace("²", " squared")
            .replace("³", " cubed")
            .replace("√", "square root of ")
            .replace("Δ", "delta ")
            .replace("θ", "theta ")
            .replace("π", "pi ")
            .replace("∠", "angle ")
            .replace("△", "triangle ")
            .replace("⇒", " implies ")
            .replace("≠", " not equal to ")
            .replace("⊥", " perpendicular to ")
            .replace("||", " parallel to ")
            .replace("*", " times ")
    }
}
