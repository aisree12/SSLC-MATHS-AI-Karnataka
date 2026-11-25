package com.example.data.ksmke

/** Official 2026–27 chapter structure transcribed from the English and Kannada contents pages. */
data class OfficialSyllabusChapter(
    val number: Int,
    val titleEn: String,
    val titleKn: String,
    val part: Int,
    val englishPrintedPages: String,
    val kannadaPrintedPages: String,
    val sections: List<String>,
    val kannadaParityVerified: Boolean = false
)

object OfficialSyllabus2026_27 {
    const val edition = "KTBS / NCERT Class 10 Mathematics 2026–27"
    const val chapterCount = 14
    val appendices = listOf(
        "A1 Proofs in Mathematics — English Part 1 pp. 112–131; Kannada Part 1 pp. 123–147",
        "A2 Mathematical Modelling — English Part 2 pp. 106–116; Kannada Part 2 pp. 114–126"
    )

    val chapters = listOf(
        OfficialSyllabusChapter(1, "Real Numbers", "ವಾಸ್ತವ ಸಂಖ್ಯೆಗಳು", 1, "1–9", "1–11", listOf("1.1–1.4")),
        OfficialSyllabusChapter(2, "Polynomials", "ಬಹುಪದೋಕ್ತಿಗಳು", 1, "10–23", "12–26", listOf("2.1–2.4")),
        OfficialSyllabusChapter(3, "Pair of Linear Equations in Two Variables", "ಎರಡು ಚರಾಕ್ಷರಗಳಿರುವ ರೇಖಾತ್ಮಕ ಸಮೀಕರಣಗಳ ಜೋಡಿಗಳು", 1, "24–37", "27–42", listOf("3.1–3.4")),
        OfficialSyllabusChapter(4, "Quadratic Equations", "ವರ್ಗ ಸಮೀಕರಣಗಳು", 1, "38–47", "43–55", listOf("4.1–4.5")),
        OfficialSyllabusChapter(5, "Arithmetic Progressions", "ಸಮಾಂತರ ಶ್ರೇಢಿಗಳು", 1, "48–71", "56–80", listOf("5.1–5.5")),
        OfficialSyllabusChapter(6, "Triangles", "ತ್ರಿಭುಜಗಳು", 1, "72–96", "81–107", listOf("6.1–6.5")),
        OfficialSyllabusChapter(7, "Coordinate Geometry", "ನಿರ್ದೇಶಾಂಕ ರೇಖಾಗಣಿತ", 1, "98–111", "108–122", listOf("7.1–7.4")),
        OfficialSyllabusChapter(8, "Introduction to Trigonometry", "ತ್ರಿಕೋನಮಿತಿಯ ಪ್ರಸ್ತಾವನೆ", 2, "1–20", "1–21", listOf("8.1–8.5")),
        OfficialSyllabusChapter(9, "Some Applications of Trigonometry", "ತ್ರಿಕೋನಮಿತಿಯ ಕೆಲವು ಅನ್ವಯಗಳು", 2, "21–31", "22–32", listOf("9.1–9.2")),
        OfficialSyllabusChapter(10, "Circles", "ವೃತ್ತಗಳು", 2, "32–41", "33–43", listOf("10.1–10.4")),
        OfficialSyllabusChapter(11, "Areas Related to Circles", "ವೃತ್ತಗಳಿಗೆ ಸಂಬಂಧಿಸಿದ ವಿಸ್ತೀರ್ಣಗಳು", 2, "42–48", "44–50", listOf("11.1–11.2")),
        OfficialSyllabusChapter(12, "Surface Areas and Volumes", "ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣಗಳು ಮತ್ತು ಘನಫಲಗಳು", 2, "49–58", "51–62", listOf("12.1–12.4")),
        OfficialSyllabusChapter(13, "Statistics", "ಸಂಖ್ಯಾಶಾಸ್ತ್ರ", 2, "59–88", "63–94", listOf("13.1–13.5")),
        OfficialSyllabusChapter(14, "Probability", "ಸಂಭವನೀಯತೆ", 2, "90–105", "95–113", listOf("14.1–14.2"))
    )
    // Legacy app IDs -> official IDs. Old chapter 6 (Constructions) is deliberately unmapped.
    val legacyChapterIdToOfficial: Map<Int, Int> = mapOf(
        1 to 5, 2 to 6, 3 to 3, 4 to 10, 5 to 11, 7 to 7, 8 to 1,
        9 to 2, 10 to 4, 11 to 8, 12 to 9, 13 to 13, 14 to 14, 15 to 12
    )
    val officialChapterIdToLegacy: Map<Int, Int> =
        legacyChapterIdToOfficial.entries.associate { (legacy, official) -> official to legacy }
    fun chapter(number: Int): OfficialSyllabusChapter? = chapters.firstOrNull { it.number == number }
    fun officialIdForLegacy(legacyId: Int): Int? = legacyChapterIdToOfficial[legacyId]
    fun legacyIdForOfficial(officialId: Int): Int? = officialChapterIdToLegacy[officialId]
}
