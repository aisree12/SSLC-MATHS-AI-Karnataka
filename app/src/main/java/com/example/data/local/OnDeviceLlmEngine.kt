package com.example.data.local

import android.content.Context
import android.util.Log
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "OnDeviceLlmEngine"

/**
 * Genuine Android On-Device Offline AI Inference Runtime powered by MediaPipe `tasks-genai`
 * (`LlmInference`) executing quantised open-weight `.task` weights locally on the student's phone.
 */
class OnDeviceLlmEngine(context: Context) {

    private val appContext = context.applicationContext
    private val inferenceMutex = Mutex()

    @Volatile
    private var loadedModelPath: String? = null

    @Volatile
    private var llmInference: LlmInference? = null

    private fun ensureLoaded(modelFile: File): LlmInference {
        val targetPath = modelFile.absolutePath
        val existing = llmInference
        if (existing != null && loadedModelPath == targetPath) {
            return existing
        }

        existing?.close()
        llmInference = null
        loadedModelPath = null

        Log.i(TAG, "Loading on-device quantised model from $targetPath")
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(targetPath)
            .setMaxTokens(1024)
            .build()

        return LlmInference.createFromOptions(appContext, options).also { created ->
            llmInference = created
            loadedModelPath = targetPath
        }
    }

    fun streamCompletion(
        modelFile: File,
        systemInstruction: String,
        userPrompt: String
    ): Flow<String> = callbackFlow {
        inferenceMutex.lock()
        val accumulated = StringBuilder()
        var future: ListenableFuture<String>? = null
        var finished = false
        try {
            val engine = ensureLoaded(modelFile)
            val chatMlPrompt = formatQwenChatMl(systemInstruction, userPrompt)

            val listener = ProgressListener<String> { partial, done ->
                if (!finished) {
                    if (!partial.isNullOrEmpty()) {
                        accumulated.append(partial)
                        val out = accumulated.toString().trim()
                        if (out.isNotEmpty()) {
                            trySend(out)
                        }
                    }
                    if (done) {
                        finished = true
                        close()
                    }
                }
            }

            future = engine.generateResponseAsync(chatMlPrompt, listener)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            close(e)
        }

        awaitClose {
            future?.cancel(true)
            if (inferenceMutex.isLocked) {
                runCatching { inferenceMutex.unlock() }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun complete(
        modelFile: File,
        systemInstruction: String,
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {
        inferenceMutex.withLock {
            val engine = ensureLoaded(modelFile)
            val chatMlPrompt = formatQwenChatMl(systemInstruction, userPrompt)
            engine.generateResponse(chatMlPrompt).orEmpty().trim()
        }
    }

    fun release() {
        runCatching { llmInference?.close() }
        llmInference = null
        loadedModelPath = null
    }

    companion object {
        fun formatQwenChatMl(systemInstruction: String, userPrompt: String): String {
            return buildString {
                append("<|im_start|>system\n")
                append(systemInstruction.trim())
                append("\n<|im_end|>\n<|im_start|>user\n")
                append(userPrompt.trim())
                append("\n<|im_end|>\n<|im_start|>assistant\n")
            }
        }
    }
}
