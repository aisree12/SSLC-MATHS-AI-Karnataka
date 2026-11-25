package com.example.data.local

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.domain.OnDeviceQuantizedModelSpec
import com.example.domain.OpenWeightModelCatalog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed interface OnDeviceModelStatus {
    val spec: OnDeviceQuantizedModelSpec

    data class NotDownloaded(override val spec: OnDeviceQuantizedModelSpec) : OnDeviceModelStatus

    data class Downloading(
        override val spec: OnDeviceQuantizedModelSpec,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : OnDeviceModelStatus {
        val progressFraction: Float
            get() {
                val denom = if (totalBytes > 0L) totalBytes else spec.approxBytes
                return if (denom > 0L) (downloadedBytes.toFloat() / denom.toFloat()).coerceIn(0f, 1f) else 0f
            }

        val percent: Int get() = (progressFraction * 100f).toInt().coerceIn(0, 100)
    }

    data class Ready(
        override val spec: OnDeviceQuantizedModelSpec,
        val localFilePath: String,
        val sizeBytes: Long
    ) : OnDeviceModelStatus

    data class Error(
        override val spec: OnDeviceQuantizedModelSpec,
        val message: String
    ) : OnDeviceModelStatus
}

/**
 * Manages real downloadable quantised LiteRT/MediaPipe `.task` weights in app-private storage
 * (`context.filesDir/on_device_models/`) with streaming download progress, cancellation, deletion,
 * and zero-permission Storage Access Framework import support.
 */
class OnDeviceModelManager(context: Context) {

    private val appContext = context.applicationContext
    private val modelsDir = File(appContext.filesDir, "on_device_models").apply { mkdirs() }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val downloadClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val _statuses = MutableStateFlow<Map<String, OnDeviceModelStatus>>(emptyMap())
    val statuses: StateFlow<Map<String, OnDeviceModelStatus>> = _statuses.asStateFlow()

    @Volatile
    private var activeCall: Call? = null
    private var activeDownloadJob: Job? = null

    init {
        refreshAllStatuses()
    }

    fun refreshAllStatuses() {
        val updated = OpenWeightModelCatalog.onDeviceQuantizedModels.associate { spec ->
            val file = targetFileFor(spec)
            val status = if (file.exists() && file.length() > MIN_VALID_MODEL_BYTES) {
                OnDeviceModelStatus.Ready(spec, file.absolutePath, file.length())
            } else {
                OnDeviceModelStatus.NotDownloaded(spec)
            }
            spec.modelId to status
        }
        _statuses.value = updated
    }

    fun getReadyModelFile(modelId: String): File? {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        val file = targetFileFor(spec)
        return if (file.exists() && file.length() > MIN_VALID_MODEL_BYTES) file else null
    }

    fun statusFor(modelId: String): OnDeviceModelStatus {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        return _statuses.value[spec.modelId] ?: OnDeviceModelStatus.NotDownloaded(spec)
    }

    fun startDownload(modelId: String) {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        if (_statuses.value[spec.modelId] is OnDeviceModelStatus.Downloading) return

        val ready = getReadyModelFile(spec.modelId)
        if (ready != null) {
            updateStatus(spec.modelId, OnDeviceModelStatus.Ready(spec, ready.absolutePath, ready.length()))
            return
        }

        activeDownloadJob?.cancel()
        activeDownloadJob = scope.launch {
            val partFile = File(modelsDir, "${spec.fileName}.part")
            val finalFile = targetFileFor(spec)
            updateStatus(spec.modelId, OnDeviceModelStatus.Downloading(spec, 0L, spec.approxBytes))

            try {
                val request = Request.Builder()
                    .url(spec.downloadUrl)
                    .header("User-Agent", "SSLC-Maths-AI-Android/1.0")
                    .build()
                val call = downloadClient.newCall(request)
                activeCall = call

                call.execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Download failed (HTTP ${response.code}). Check internet connection or import a local .task file.")
                    }
                    val body = response.body ?: throw IOException("Empty model response body.")
                    val totalBytes = body.contentLength().takeIf { it > 0L } ?: spec.approxBytes

                    body.byteStream().use { input ->
                        FileOutputStream(partFile).use { output ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE * 4)
                            var downloaded = 0L
                            var lastUiEmit = 0L
                            while (true) {
                                val read = input.read(buffer)
                                if (read == -1) break
                                output.write(buffer, 0, read)
                                downloaded += read
                                val now = System.currentTimeMillis()
                                if (now - lastUiEmit >= 250L) {
                                    lastUiEmit = now
                                    updateStatus(
                                        spec.modelId,
                                        OnDeviceModelStatus.Downloading(spec, downloaded, totalBytes)
                                    )
                                }
                            }
                            output.flush()
                        }
                    }
                }

                if (!partFile.exists() || partFile.length() < MIN_VALID_MODEL_BYTES) {
                    partFile.delete()
                    throw IOException("Downloaded file was incomplete or invalid.")
                }

                if (finalFile.exists()) finalFile.delete()
                if (!partFile.renameTo(finalFile)) {
                    throw IOException("Failed to move downloaded model into app storage.")
                }
                updateStatus(
                    spec.modelId,
                    OnDeviceModelStatus.Ready(spec, finalFile.absolutePath, finalFile.length())
                )
            } catch (e: CancellationException) {
                partFile.delete()
                updateStatus(spec.modelId, OnDeviceModelStatus.NotDownloaded(spec))
                throw e
            } catch (e: Exception) {
                partFile.delete()
                updateStatus(
                    spec.modelId,
                    OnDeviceModelStatus.Error(
                        spec,
                        e.localizedMessage ?: "Failed to download quantised model weights."
                    )
                )
            } finally {
                activeCall = null
            }
        }
    }

    fun cancelDownload(modelId: String) {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        activeCall?.cancel()
        activeDownloadJob?.cancel()
        File(modelsDir, "${spec.fileName}.part").delete()
        updateStatus(spec.modelId, OnDeviceModelStatus.NotDownloaded(spec))
    }

    fun deleteModel(modelId: String) {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        targetFileFor(spec).delete()
        File(modelsDir, "${spec.fileName}.part").delete()
        updateStatus(spec.modelId, OnDeviceModelStatus.NotDownloaded(spec))
    }

    /**
     * Imports a user-selected local `.task` / `.bin` model bundle via SAF (`OpenDocument`)
     * into app-private storage so offline inference works without internet downloads.
     */
    suspend fun importLocalModelFile(modelId: String, sourceUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        val partFile = File(modelsDir, "${spec.fileName}.import.part")
        val finalFile = targetFileFor(spec)
        runCatching {
            val displayName = resolveDisplayName(sourceUri) ?: spec.fileName
            updateStatus(spec.modelId, OnDeviceModelStatus.Downloading(spec, 0L, spec.approxBytes))
            val input = appContext.contentResolver.openInputStream(sourceUri)
                ?: throw IOException("Cannot read selected file ($displayName).")
            input.use { stream ->
                FileOutputStream(partFile).use { out ->
                    stream.copyTo(out, bufferSize = DEFAULT_BUFFER_SIZE * 4)
                    out.flush()
                }
            }
            if (partFile.length() < MIN_VALID_MODEL_BYTES) {
                partFile.delete()
                throw IOException("Selected file ($displayName) is too small to be a valid MediaPipe .task model.")
            }
            if (finalFile.exists()) finalFile.delete()
            if (!partFile.renameTo(finalFile)) {
                throw IOException("Could not save imported model file.")
            }
            updateStatus(
                spec.modelId,
                OnDeviceModelStatus.Ready(spec, finalFile.absolutePath, finalFile.length())
            )
            finalFile.absolutePath
        }.onFailure { err ->
            partFile.delete()
            updateStatus(
                spec.modelId,
                OnDeviceModelStatus.Error(spec, err.localizedMessage ?: "Import failed")
            )
        }
    }

    private fun resolveDisplayName(uri: Uri): String? {
        return runCatching {
            appContext.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                }
        }.getOrNull()
    }

    private fun targetFileFor(spec: OnDeviceQuantizedModelSpec): File =
        File(modelsDir, spec.fileName)

    private fun updateStatus(modelId: String, status: OnDeviceModelStatus) {
        _statuses.value = _statuses.value + (modelId to status)
    }

    companion object {
        private const val MIN_VALID_MODEL_BYTES = 10L * 1024L * 1024L // 10 MB sanity floor for real model bundles
    }
}
