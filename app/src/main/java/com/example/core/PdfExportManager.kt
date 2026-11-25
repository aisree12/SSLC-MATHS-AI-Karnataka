package com.example.core

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.domain.ChapterQuestionPaper
import com.example.domain.KsmkeChapter
import com.example.domain.TopicDetailedNote
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

data class PdfExportResult(
    val fileName: String,
    val savedLocationLabel: String,
    val shareUri: Uri
)

/**
 * Generates clean, paginated A4 PDFs using Android's native PdfDocument API
 * and saves them directly to the user's phone Downloads/SSLC_Maths_AI folder
 * (zero storage permissions needed on Android 10+) plus app-scoped FileProvider URI.
 */
object PdfExportManager {

    private const val PAGE_WIDTH = 595 // A4 at 72 dpi
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 42f
    private const val CONTENT_WIDTH = PAGE_WIDTH - 2 * 42f
    private const val TOP_Y = 54f
    private const val BOTTOM_LIMIT = PAGE_HEIGHT - 48f

    fun exportChapterNotesPdf(
        context: Context,
        chapter: KsmkeChapter,
        topics: List<TopicDetailedNote>,
        aiDeepDiveMarkdown: String? = null
    ): Result<PdfExportResult> = runCatching {
        val doc = PdfDocument()
        val state = PageWriterState(doc)

        state.drawHeaderBanner(
            title = "KSMKE Detailed Notes • Ch ${chapter.chapterNumber}: ${chapter.titleEn}",
            subtitle = "${chapter.titleKn} • ${chapter.textbookEdition} (${chapter.pageRange}) • Board Weightage: ${chapter.boardMarksWeightage} Marks"
        )

        state.writeSectionHeading("Chapter Overview & Blueprint")
        state.writeBody(chapter.summaryEn)
        state.writeSubtle(chapter.summaryKn)
        state.addGap(8f)

        state.writeSectionHeading("Complete Formula Sheet")
        chapter.keyFormulas.forEach { formula ->
            state.writeMono("• $formula")
        }
        state.addGap(8f)

        if (chapter.theorems.isNotEmpty()) {
            state.writeSectionHeading("Prescribed Board Theorems & Proofs")
            chapter.theorems.forEach { th ->
                state.writeSubHeading("${th.name} (${th.kannadaName}) — ${th.marksWeight} Marks")
                state.writeBody("Statement: ${th.statement}")
                th.keyProofSteps.forEach { step ->
                    state.writeBody(step)
                }
                state.addGap(6f)
            }
        }

        topics.forEach { topic ->
            state.writeSectionHeading("Topic ${topic.topicNumber}: ${topic.topicTitleEn}")
            state.writeSubtle(topic.topicTitleKn)

            if (topic.coreConcepts.isNotEmpty()) {
                state.writeSubHeading("Core Concepts & Definitions")
                topic.coreConcepts.forEach { c -> state.writeBody("• $c") }
            }
            if (topic.formulasAndRules.isNotEmpty()) {
                state.writeSubHeading("Formulas & Rules")
                topic.formulasAndRules.forEach { f -> state.writeMono("• $f") }
            }
            if (topic.derivationsOrMethods.isNotEmpty()) {
                state.writeSubHeading("Step-by-Step Method / Derivation")
                topic.derivationsOrMethods.forEach { d -> state.writeBody(d) }
            }
            if (topic.workedExamples.isNotEmpty()) {
                state.writeSubHeading("Worked Examples (Complete Marking Steps)")
                topic.workedExamples.forEach { ex ->
                    state.writeBody("Example: ${ex.title}")
                    state.writeBody("Q: ${ex.problem}")
                    ex.markingSteps.forEach { step -> state.writeMono("  $step") }
                    state.writeBold("Final Answer: ${ex.finalAnswer}")
                    state.writeSubtle("Examiner Tip: ${ex.examinerTip}")
                    state.addGap(4f)
                }
            }
            if (topic.examMistakesToAvoid.isNotEmpty()) {
                state.writeSubHeading("Common Mistakes to Avoid")
                topic.examMistakesToAvoid.forEach { m -> state.writeBody("⚠ $m") }
            }
            state.addGap(8f)
        }

        if (!aiDeepDiveMarkdown.isNullOrBlank()) {
            state.writeSectionHeading("AI Extended Topic-Wise Deep Notes")
            aiDeepDiveMarkdown.lines().forEach { line ->
                val clean = line.replace("**", "").replace("##", "").trim()
                if (clean.isNotEmpty()) state.writeBody(clean)
            }
        }

        state.finish()
        val safeTitle = chapter.titleEn.replace(Regex("[^a-zA-Z0-9]+"), "_").trim('_')
        val fileName = "SSLC_Ch${chapter.chapterNumber}_${safeTitle}_Detailed_Notes.pdf"
        savePdfBytes(context, doc, fileName)
    }

    fun exportQuestionPaperPdf(
        context: Context,
        paper: ChapterQuestionPaper,
        includeAnswerKey: Boolean
    ): Result<PdfExportResult> = runCatching {
        val doc = PdfDocument()
        val state = PageWriterState(doc)

        state.drawHeaderBanner(
            title = paper.paperTitle,
            subtitle = "Ch ${paper.chapterNumber}: ${paper.chapterTitleEn} (${paper.chapterTitleKn}) • ${paper.totalMarks} Marks • ${paper.durationMinutes} Min"
        )

        state.writeBody("PYQ & Probability Blueprint: ${paper.pyqBlueprintSummary}")
        state.writeSubtle("Note: Historical PYQ targeting and practice probability reflect past KSEAB frequency patterns for focused preparation.")
        state.addGap(8f)

        val grouped = paper.questions.groupBy { it.sectionLabel }
        grouped.forEach { (section, qList) ->
            state.writeSectionHeading(section)
            qList.forEach { q ->
                val originTag = if (q.isAiInvented) "AI Targeted Practice" else "PYQ Pattern"
                state.writeSubHeading("Q${q.questionNumber}. [${q.marks} Mark(s) • ${q.probabilityBadge} • $originTag]")
                state.writeBody(q.questionEn)
                if (q.questionKn.isNotBlank()) {
                    state.writeSubtle(q.questionKn)
                }
                if (q.mcqOptions.isNotEmpty()) {
                    val labels = listOf("A", "B", "C", "D")
                    q.mcqOptions.forEachIndexed { idx, opt ->
                        state.writeMono("   (${labels.getOrElse(idx) { "${idx + 1}" }}) $opt")
                    }
                }
                state.writeSubtle("Target Note: ${q.pyqTargetNote}")
                state.addGap(6f)
            }
        }

        if (includeAnswerKey) {
            state.startNewPage()
            state.drawHeaderBanner(
                title = "Official Marking Scheme & Step Solutions",
                subtitle = "${paper.paperTitle} • Complete Step-by-Step Key Answers"
            )
            paper.questions.forEach { q ->
                state.writeSubHeading("Q${q.questionNumber} Answer (${q.marks}M): ${q.officialAnswer}")
                q.stepByStepSolution.forEach { step ->
                    state.writeMono("  • $step")
                }
                state.writeSubtle("Common Mistake: ${q.commonMistake}")
                state.addGap(6f)
            }
        }

        state.finish()
        val safeTitle = paper.chapterTitleEn.replace(Regex("[^a-zA-Z0-9]+"), "_").trim('_')
        val suffix = if (includeAnswerKey) "Paper_With_Solutions" else "Question_Paper"
        val fileName = "SSLC_Ch${paper.chapterNumber}_${safeTitle}_$suffix.pdf"
        savePdfBytes(context, doc, fileName)
    }

    private fun savePdfBytes(
        context: Context,
        doc: PdfDocument,
        fileName: String
    ): PdfExportResult {
        val baos = ByteArrayOutputStream()
        doc.writeTo(baos)
        doc.close()
        val pdfBytes = baos.toByteArray()

        // 1. Save a shareable copy in app-scoped Documents folder for immediate FileProvider viewing
        val docsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir, "SSLC_Maths_AI")
        if (!docsDir.exists()) docsDir.mkdirs()
        val appFile = File(docsDir, fileName)
        FileOutputStream(appFile).use { it.write(pdfBytes) }
        val shareUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            appFile
        )

        // 2. Also save into the public phone Downloads/SSLC_Maths_AI folder on Android 10+ (zero permissions)
        var savedLocation = "App Documents/SSLC_Maths_AI/$fileName"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/SSLC_Maths_AI")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val downloadUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (downloadUri != null) {
                resolver.openOutputStream(downloadUri)?.use { out ->
                    out.write(pdfBytes)
                }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(downloadUri, values, null, null)
                savedLocation = "Downloads/SSLC_Maths_AI/$fileName"
            }
        }

        return PdfExportResult(
            fileName = fileName,
            savedLocationLabel = savedLocation,
            shareUri = shareUri
        )
    }

    fun openPdfIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun sharePdfIntent(uri: Uri, title: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.let { Intent.createChooser(it, "Share or Save PDF") }
    }

    private class PageWriterState(private val doc: PdfDocument) {
        private var pageNumber = 0
        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var y = TOP_Y

        private val bannerBgPaint = Paint().apply {
            color = Color.rgb(29, 53, 87) // IndigoInk
            style = Paint.Style.FILL
        }
        private val bannerTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        private val bannerSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(254, 243, 199)
            textSize = 9.5f
        }
        private val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(29, 53, 87)
            textSize = 12.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        private val subHeadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 118, 110) // EmeraldChalk
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            textSize = 10f
        }
        private val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        private val monoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(17, 34, 58)
            textSize = 9.5f
            typeface = Typeface.MONOSPACE
        }
        private val subtlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 116, 139)
            textSize = 9f
        }
        private val dividerPaint = Paint().apply {
            color = Color.rgb(214, 207, 194)
            strokeWidth = 1f
        }

        init {
            startNewPage()
        }

        fun startNewPage() {
            page?.let {
                drawFooter()
                doc.finishPage(it)
            }
            pageNumber++
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = doc.startPage(info)
            canvas = page!!.canvas
            canvas!!.drawColor(Color.WHITE)
            y = TOP_Y
        }

        private fun drawFooter() {
            val c = canvas ?: return
            c.drawLine(MARGIN_X, PAGE_HEIGHT - 34f, PAGE_WIDTH - MARGIN_X, PAGE_HEIGHT - 34f, dividerPaint)
            c.drawText(
                "SSLC Maths AI • Karnataka Class 10 KSMKE   |   Page $pageNumber",
                MARGIN_X,
                PAGE_HEIGHT - 20f,
                subtlePaint
            )
        }

        private fun ensureSpace(neededHeight: Float) {
            if (y + neededHeight > BOTTOM_LIMIT) {
                startNewPage()
            }
        }

        fun drawHeaderBanner(title: String, subtitle: String) {
            val titleLines = wrapText(title, bannerTitlePaint, CONTENT_WIDTH - 24f)
            val subLines = wrapText(subtitle, bannerSubPaint, CONTENT_WIDTH - 24f)
            val height = 22f + titleLines.size * 19f + subLines.size * 13f
            ensureSpace(height + 12f)
            val c = canvas!!
            c.drawRoundRect(MARGIN_X, y, PAGE_WIDTH - MARGIN_X, y + height, 10f, 10f, bannerBgPaint)
            var ty = y + 22f
            titleLines.forEach {
                c.drawText(it, MARGIN_X + 12f, ty, bannerTitlePaint)
                ty += 18f
            }
            subLines.forEach {
                c.drawText(it, MARGIN_X + 12f, ty, bannerSubPaint)
                ty += 13f
            }
            y += height + 14f
        }

        fun writeSectionHeading(text: String) {
            ensureSpace(26f)
            addGap(4f)
            writeWrapped(text, sectionPaint, 16f)
            canvas?.drawLine(MARGIN_X, y - 2f, PAGE_WIDTH - MARGIN_X, y - 2f, dividerPaint)
            addGap(4f)
        }

        fun writeSubHeading(text: String) {
            ensureSpace(18f)
            writeWrapped(text, subHeadPaint, 14f)
        }

        fun writeBody(text: String) = writeWrapped(text, bodyPaint, 13.5f)
        fun writeBold(text: String) = writeWrapped(text, boldPaint, 13.5f)
        fun writeMono(text: String) = writeWrapped(text, monoPaint, 13f)
        fun writeSubtle(text: String) = writeWrapped(text, subtlePaint, 12.5f)

        fun addGap(dp: Float) {
            y += dp
        }

        private fun writeWrapped(text: String, paint: Paint, lineHeight: Float) {
            val lines = wrapText(text, paint, CONTENT_WIDTH)
            lines.forEach { line ->
                ensureSpace(lineHeight + 4f)
                canvas?.drawText(line, MARGIN_X, y, paint)
                y += lineHeight
            }
            y += 2f
        }

        private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
            if (text.isBlank()) return listOf("")
            val result = mutableListOf<String>()
            val words = text.split(Regex("\\s+"))
            var current = StringBuilder()
            for (word in words) {
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    current = StringBuilder(candidate)
                } else {
                    if (current.isNotEmpty()) {
                        result.add(current.toString())
                    }
                    // Handle long single token
                    if (paint.measureText(word) > maxWidth) {
                        var rest = word
                        while (rest.isNotEmpty()) {
                            val count = paint.breakText(rest, true, maxWidth, null).coerceAtLeast(1)
                            result.add(rest.substring(0, count))
                            rest = rest.substring(count)
                        }
                        current = StringBuilder()
                    } else {
                        current = StringBuilder(word)
                    }
                }
            }
            if (current.isNotEmpty()) result.add(current.toString())
            return result
        }

        fun finish() {
            page?.let {
                drawFooter()
                doc.finishPage(it)
            }
            page = null
        }
    }
}
