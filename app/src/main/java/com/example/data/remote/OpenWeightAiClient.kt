package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.domain.OpenWeightModelCatalog
import com.example.domain.OpenWeightProvider
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private const val TAG = "OpenWeightAiClient"
private const val PLACEHOLDER_KEY = "MY_FREE_OPEN_AI_API_KEY"
private const val PLACEHOLDER_GEMINI_KEY = "YOUR_GEMINI_API_KEY"
private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

/**
 * Unified AI Gateway supporting:
 * - Google Gemini (Firebase AI Logic with keyless App Check attestation: gemini-3.8-flash, gemini-3.5-flash-lite)
 * - OpenRouter (:free), Groq, Hugging Face Router, Cerebras Free Tier, and Self-Hosted Ollama.
 */
class OpenWeightAiClient {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Lazy cache so Firebase.ai is never evaluated at class construction time.
    private val geminiModels = ConcurrentHashMap<Pair<String, String>, GenerativeModel>()

    private fun getOrCreateGeminiModel(modelId: String, systemInstruction: String): GenerativeModel {
        return geminiModels.getOrPut(modelId to systemInstruction) {
            Firebase.ai.generativeModel(
                modelName = modelId,
                generationConfig = generationConfig {
                    temperature = 0.35f
                    maxOutputTokens = 2048
                },
                systemInstruction = if (systemInstruction.isNotBlank()) {
                    content { text(systemInstruction) }
                } else null
            )
        }
    }

    private fun isUsableKey(key: String, placeholder: String): Boolean =
        key.isNotBlank() && key != placeholder

    fun isApiKeyConfigured(): Boolean =
        isUsableKey(BuildConfig.OPENAI_COMPAT_API_KEY.trim(), PLACEHOLDER_KEY) ||
            isUsableKey(BuildConfig.GEMINI_API_KEY.trim(), PLACEHOLDER_GEMINI_KEY)

    fun isProviderApiKeyConfigured(provider: OpenWeightProvider): Boolean = when (provider) {
        OpenWeightProvider.GEMINI_API_KEY -> isUsableKey(BuildConfig.GEMINI_API_KEY.trim(), PLACEHOLDER_GEMINI_KEY)
        else -> isUsableKey(BuildConfig.OPENAI_COMPAT_API_KEY.trim(), PLACEHOLDER_KEY)
    }

    private fun apiKeyFor(provider: OpenWeightProvider): String = when (provider) {
        OpenWeightProvider.GEMINI_API_KEY -> BuildConfig.GEMINI_API_KEY.trim()
        else -> BuildConfig.OPENAI_COMPAT_API_KEY.trim()
    }

    fun streamChatCompletion(
        provider: OpenWeightProvider,
        modelId: String,
        systemInstruction: String,
        userPrompt: String
    ): Flow<String> = flow {
        ensureProviderReady(provider)
        val candidates = candidateModelsFor(provider, modelId, requireVision = false)
        var lastError: Throwable? = null

        if (provider == OpenWeightProvider.GEMINI) {
            for (candidate in candidates) {
                try {
                    val model = getOrCreateGeminiModel(candidate, systemInstruction)
                    val accumulated = StringBuilder()
                    withTimeout(60_000L) {
                        model.generateContentStream(userPrompt).collect { chunk ->
                            val delta = chunk.text.orEmpty()
                            if (delta.isNotEmpty()) {
                                accumulated.append(delta)
                                val cleaned = stripThinkingBlocks(accumulated.toString())
                                if (cleaned.isNotBlank()) {
                                    emit(cleaned)
                                }
                            }
                        }
                    }
                    val finalOut = stripThinkingBlocks(accumulated.toString())
                    if (finalOut.isNotBlank()) return@flow
                    lastError = IOException("Gemini model $candidate returned an empty streamed reply.")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    lastError = e
                    Log.w(TAG, "Gemini candidate $candidate failed: ${e.message}")
                }
            }
            throw lastError ?: IOException("Gemini streaming failed.")
        }

        for (candidate in candidates) {
            try {
                val payload = buildChatPayload(
                    modelId = candidate,
                    systemInstruction = systemInstruction,
                    userPrompt = userPrompt,
                    stream = true
                )
                val request = buildHttpRequest(provider, payload)
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errBody = response.body?.string().orEmpty().take(300)
                        val ex = IOException("HTTP ${response.code} (${candidate}): $errBody")
                        if (shouldTryNextFallback(response.code)) {
                            lastError = ex
                            Log.w(TAG, "Free model $candidate returned ${response.code}, trying fallback")
                            return@use
                        }
                        throw ex
                    }

                    val source = response.body?.source()
                        ?: throw IOException("Empty streaming response body from ${provider.displayName}")
                    val accumulated = StringBuilder()
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: break
                        if (!line.startsWith("data:")) continue
                        val data = line.removePrefix("data:").trim()
                        if (data == "[DONE]") break
                        if (data.isBlank()) continue
                        val delta = parseSseDeltaContent(data)
                        if (delta.isNotEmpty()) {
                            accumulated.append(delta)
                            val cleaned = stripThinkingBlocks(accumulated.toString())
                            if (cleaned.isNotBlank()) {
                                emit(cleaned)
                            }
                        }
                    }

                    val finalOut = stripThinkingBlocks(accumulated.toString())
                    if (finalOut.isBlank()) {
                        throw IOException("Model $candidate returned an empty streamed reply.")
                    }
                    return@flow
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
                Log.w(TAG, "Candidate $candidate failed: ${e.message}")
            }
        }

        throw lastError ?: IOException("All free models failed for ${provider.displayName}.")
    }.flowOn(Dispatchers.IO)

    suspend fun completeChat(
        provider: OpenWeightProvider,
        modelId: String,
        systemInstruction: String,
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {
        ensureProviderReady(provider)
        val candidates = candidateModelsFor(provider, modelId, requireVision = false)
        var lastError: Throwable? = null

        if (provider == OpenWeightProvider.GEMINI) {
            for (candidate in candidates) {
                try {
                    val model = getOrCreateGeminiModel(candidate, systemInstruction)
                    val response = withTimeout(60_000L) {
                        model.generateContent(userPrompt)
                    }
                    val text = stripThinkingBlocks(response.text.orEmpty())
                    if (text.isNotBlank()) return@withContext text
                    lastError = IOException("Empty completion from Gemini $candidate")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    lastError = e
                    Log.w(TAG, "Gemini completion $candidate failed: ${e.message}")
                }
            }
            throw lastError ?: IOException("Failed to get completion from Google Gemini.")
        }

        for (candidate in candidates) {
            try {
                val payload = buildChatPayload(
                    modelId = candidate,
                    systemInstruction = systemInstruction,
                    userPrompt = userPrompt,
                    stream = false
                )
                val request = buildHttpRequest(provider, payload)
                httpClient.newCall(request).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        val ex = IOException("HTTP ${response.code} ($candidate): ${raw.take(300)}")
                        if (shouldTryNextFallback(response.code)) {
                            lastError = ex
                            return@use
                        }
                        throw ex
                    }
                    val text = stripThinkingBlocks(parseNonStreamingContent(raw))
                    if (text.isNotBlank()) return@withContext text
                    lastError = IOException("Empty completion from $candidate")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IOException("Failed to get completion from ${provider.displayName}")
    }

    suspend fun analyzeImageWithVisionModel(
        provider: OpenWeightProvider,
        bitmap: Bitmap,
        systemInstruction: String,
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {
        ensureProviderReady(provider)
        val visionModel = OpenWeightModelCatalog.defaultVisionModel(provider).id
        val fallbackVision = OpenWeightModelCatalog.models
            .filter { it.provider == provider && it.supportsVision }
            .map { it.id }
            .ifEmpty { listOf(visionModel) }

        var lastError: Throwable? = null

        if (provider == OpenWeightProvider.GEMINI) {
            val safeBitmap = scaleBitmapForPayload(bitmap)
            for (candidate in fallbackVision) {
                try {
                    val model = getOrCreateGeminiModel(candidate, systemInstruction)
                    val response = withTimeout(60_000L) {
                        model.generateContent(
                            content {
                                image(safeBitmap)
                                text(userPrompt)
                            }
                        )
                    }
                    val text = stripThinkingBlocks(response.text.orEmpty())
                    if (text.isNotBlank()) return@withContext text
                    lastError = IOException("Empty Gemini vision response from $candidate")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    lastError = e
                    Log.w(TAG, "Gemini vision candidate $candidate failed: ${e.message}")
                }
            }
            throw lastError ?: IOException("Vision analysis failed on Google Gemini.")
        }

        val dataUri = bitmapToDataUri(bitmap)

        for (candidate in fallbackVision) {
            try {
                val contentArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("type", "text")
                        put("text", userPrompt)
                    })
                    put(JSONObject().apply {
                        put("type", "image_url")
                        put("image_url", JSONObject().apply {
                            put("url", dataUri)
                        })
                    })
                }
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemInstruction)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", contentArray)
                    })
                }
                val payload = JSONObject().apply {
                    put("model", candidate)
                    put("messages", messages)
                    put("temperature", 0.2)
                    put("max_tokens", 1800)
                    put("stream", false)
                }
                val request = buildHttpRequest(provider, payload)
                httpClient.newCall(request).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        val ex = IOException("Vision HTTP ${response.code} ($candidate): ${raw.take(300)}")
                        if (shouldTryNextFallback(response.code)) {
                            lastError = ex
                            return@use
                        }
                        throw ex
                    }
                    val text = stripThinkingBlocks(parseNonStreamingContent(raw))
                    if (text.isNotBlank()) return@withContext text
                    lastError = IOException("Empty vision response from $candidate")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IOException("Vision analysis failed on ${provider.displayName}")
    }

    private fun scaleBitmapForPayload(bitmap: Bitmap, maxDim: Int = 1440): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDim && h <= maxDim) return bitmap
        val ratio = minOf(maxDim.toFloat() / w.toFloat(), maxDim.toFloat() / h.toFloat())
        return Bitmap.createScaledBitmap(
            bitmap,
            (w * ratio).toInt().coerceAtLeast(1),
            (h * ratio).toInt().coerceAtLeast(1),
            true
        )
    }

    private fun ensureProviderReady(provider: OpenWeightProvider) {
        if (provider == OpenWeightProvider.ON_DEVICE_OFFLINE) {
            throw IllegalStateException("On-Device Offline AI runs locally via MediaPipe, not HTTP.")
        }
        if (provider.requiresApiKey && !isProviderApiKeyConfigured(provider)) {
            throw IllegalStateException(
                "API key missing for ${provider.displayName}. Add GEMINI_API_KEY (Google AI Studio) or OPENAI_COMPAT_API_KEY (other compatible providers) to local Secrets. " +
                    "Alternatively select Firebase Gemini or On-Device AI."
            )
        }
    }

    private fun buildHttpRequest(provider: OpenWeightProvider, payload: JSONObject): Request {
        val builder = Request.Builder()
            .url(provider.chatCompletionsUrl)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .header("Content-Type", "application/json")
        if (provider.requiresApiKey) {
            builder.header("Authorization", "Bearer ${apiKeyFor(provider)}")
        }
        if (provider == OpenWeightProvider.OPENROUTER_FREE) {
            builder.header("HTTP-Referer", "https://sslc-maths-ai.local")
            builder.header("X-Title", "SSLC Maths AI Karnataka")
        }
        return builder.build()
    }

    private fun buildChatPayload(
        modelId: String,
        systemInstruction: String,
        userPrompt: String,
        stream: Boolean
    ): JSONObject {
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", systemInstruction)
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", userPrompt)
            })
        }
        return JSONObject().apply {
            put("model", modelId)
            put("messages", messages)
            put("temperature", 0.35)
            put("max_tokens", 2048)
            put("stream", stream)
        }
    }

    private fun candidateModelsFor(
        provider: OpenWeightProvider,
        primaryModelId: String,
        requireVision: Boolean
    ): List<String> {
        val all = OpenWeightModelCatalog.modelsFor(provider)
            .filter { !requireVision || it.supportsVision }
            .map { it.id }
        return (listOf(primaryModelId) + all).distinct()
    }

    private fun shouldTryNextFallback(httpCode: Int): Boolean =
        httpCode == 429 || httpCode == 503 || httpCode == 502 || httpCode == 404

    private fun parseSseDeltaContent(jsonLine: String): String {
        return runCatching {
            val obj = JSONObject(jsonLine)
            val choices = obj.optJSONArray("choices") ?: return ""
            if (choices.length() == 0) return ""
            val first = choices.getJSONObject(0)
            val delta = first.optJSONObject("delta")
            delta?.optString("content", "")
                ?: first.optJSONObject("message")?.optString("content", "")
                ?: ""
        }.getOrDefault("")
    }

    private fun parseNonStreamingContent(rawJson: String): String {
        val obj = JSONObject(rawJson)
        val choices = obj.getJSONArray("choices")
        if (choices.length() == 0) return ""
        val message = choices.getJSONObject(0).getJSONObject("message")
        return message.optString("content", "").trim()
    }

    private fun bitmapToDataUri(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        val b64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$b64"
    }

    companion object {
        fun stripThinkingBlocks(text: String): String {
            return text
                .replace(Regex("<think>[\\s\\S]*?</think>"), "")
                .replace(Regex("<think>[\\s\\S]*$"), "")
                .trim()
        }

        fun extractJsonString(rawText: String): String {
            val cleaned = stripThinkingBlocks(rawText)
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim()
            val start = cleaned.indexOf('{')
            val end = cleaned.lastIndexOf('}')
            if (start >= 0 && end > start) {
                return cleaned.substring(start, end + 1)
            }
            throw IllegalStateException("Open-weight model response did not contain valid JSON.")
        }

        fun extractJsonObject(rawText: String): JSONObject =
            JSONObject(extractJsonString(rawText))
    }
}
