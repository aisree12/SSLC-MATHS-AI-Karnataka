package com.example.data.ksmke

/** Official 2026–27 chapter structure transcribed from the English and Kannada contents pages. */
data class OfficialSyllabusChapter(
    val number: Int,
    val titleEn: String,
    val titleKn: String,
    val part: Int,
    val englishPrintedPages: String,
    val kannadaPrintedPages: String,
    /** Exact English section headings and printed page numbers from the official contents pages. */
    val sections: List<String>,
    /** Exercise IDs found in the English textbook pages; Kannada exercise-by-exercise parity is pending. */
    val exerciseIds: List<String>,
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
        OfficialSyllabusChapter(1, "Real Numbers", "ವಾಸ್ತವ ಸಂಖ್ಯೆಗಳು", 1, "1–9", "1–11", listOf("1.1 Introduction (p. 1)", "1.2 The Fundamental Theorem of Arithmetic (p. 2)", "1.3 Revisiting Irrational Numbers (p. 6)", "1.4 Summary (p. 9)"), listOf("1.1", "1.2")),
        OfficialSyllabusChapter(2, "Polynomials", "ಬಹುಪದೋಕ್ತಿಗಳು", 1, "10–23", "12–26", listOf("2.1 Introduction (p. 10)", "2.2 Geometrical Meaning of the Zeroes of a Polynomial (p. 11)", "2.3 Relationship between Zeroes and Coefficients of a Polynomial (p. 18)", "2.4 Summary (p. 23)"), listOf("2.1", "2.2")),
        OfficialSyllabusChapter(3, "Pair of Linear Equations in Two Variables", "ಎರಡು ಚರಾಕ್ಷರಗಳಿರುವ ರೇಖಾತ್ಮಕ ಸಮೀಕರಣಗಳ ಜೋಡಿಗಳು", 1, "24–37", "27–42", listOf("3.1 Introduction (p. 24)", "3.2 Graphical Method of Solution of a Pair of Linear Equations (p. 25)", "3.3 Algebraic Methods of Solving a Pair of Linear Equations (p. 30)", "3.3.1 Substitution Method (p. 30)", "3.3.2 Elimination Method (p. 34)", "3.4 Summary (p. 37)"), listOf("3.1", "3.2", "3.3")),
        OfficialSyllabusChapter(4, "Quadratic Equations", "ವರ್ಗ ಸಮೀಕರಣಗಳು", 1, "38–47", "43–55", listOf("4.1 Introduction (p. 38)", "4.2 Quadratic Equations (p. 39)", "4.3 Solution of a Quadratic Equation by Factorisation (p. 42)", "4.4 Nature of Roots (p. 44)", "4.5 Summary (p. 47)"), listOf("4.1", "4.2", "4.3")),
        OfficialSyllabusChapter(5, "Arithmetic Progressions", "ಸಮಾಂತರ ಶ್ರೇಢಿಗಳು", 1, "48–71", "56–80", listOf("5.1 Introduction (p. 48)", "5.2 Arithmetic Progressions (p. 50)", "5.3 nth Term of an AP (p. 55)", "5.4 Sum of First n Terms of an AP (p. 62)", "5.5 Summary (p. 71)"), listOf("5.1", "5.2", "5.3", "5.4")),
        OfficialSyllabusChapter(6, "Triangles", "ತ್ರಿಭುಜಗಳು", 1, "72–96", "81–107", listOf("6.1 Introduction (p. 72)", "6.2 Similar Figures (p. 73)", "6.3 Similarity of Triangles (p. 78)", "6.4 Criteria for Similarity of Triangles (p. 84)", "6.5 Summary (p. 96)"), listOf("6.1", "6.2", "6.3")),
        OfficialSyllabusChapter(7, "Coordinate Geometry", "ನಿರ್ದೇಶಾಂಕ ರೇಖಾಗಣಿತ", 1, "98–111", "108–122", listOf("7.1 Introduction (p. 98)", "7.2 Distance Formula (p. 99)", "7.3 Section Formula (p. 105)", "7.4 Summary (p. 111)"), listOf("7.1", "7.2")),
        OfficialSyllabusChapter(8, "Introduction to Trigonometry", "ತ್ರಿಕೋನಮಿತಿಯ ಪ್ರಸ್ತಾವನೆ", 2, "1–20", "1–21", listOf("8.1 Introduction (p. 1)", "8.2 Trigonometric Ratios (p. 2)", "8.3 Trigonometric Ratios of Some Specific Angles (p. 9)", "8.4 Trigonometric Identities (p. 16)", "8.5 Summary (p. 20)"), listOf("8.1", "8.2", "8.3")),
        OfficialSyllabusChapter(9, "Some Applications of Trigonometry", "ತ್ರಿಕೋನಮಿತಿಯ ಕೆಲವು ಅನ್ವಯಗಳು", 2, "21–31", "22–32", listOf("9.1 Heights and Distances (p. 21)", "9.2 Summary (p. 31)"), listOf("9.1")),
        OfficialSyllabusChapter(10, "Circles", "ವೃತ್ತಗಳು", 2, "32–41", "33–43", listOf("10.1 Introduction (p. 32)", "10.2 Tangent to a Circle (p. 33)", "10.3 Number of Tangents from a Point on a Circle (p. 35)", "10.4 Summary (p. 41)"), listOf("10.1", "10.2")),
        OfficialSyllabusChapter(11, "Areas Related to Circles", "ವೃತ್ತಗಳಿಗೆ ಸಂಬಂಧಿಸಿದ ವಿಸ್ತೀರ್ಣಗಳು", 2, "42–48", "44–50", listOf("11.1 Areas of Sector and Segment of a Circle (p. 42)", "11.2 Summary (p. 48)"), listOf("11.1")),
        OfficialSyllabusChapter(12, "Surface Areas and Volumes", "ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣಗಳು ಮತ್ತು ಘನಫಲಗಳು", 2, "49–58", "51–62", listOf("12.1 Introduction (p. 49)", "12.2 Surface Area of a Combination of Solids (p. 50)", "12.3 Volume of a Combination of Solids (p. 55)", "12.4 Summary (p. 58)"), listOf("12.1", "12.2")),
        OfficialSyllabusChapter(13, "Statistics", "ಸಂಖ್ಯಾಶಾಸ್ತ್ರ", 2, "59–88", "63–94", listOf("13.1 Introduction (p. 59)", "13.2 Mean of Grouped Data (p. 59)", "13.3 Mode of Grouped Data (p. 71)", "13.4 Median of Grouped Data (p. 76)", "13.5 Summary (p. 88)"), listOf("13.1", "13.2", "13.3")),
        OfficialSyllabusChapter(14, "Probability", "ಸಂಭವನೀಯತೆ", 2, "90–105", "95–113", listOf("14.1 Probability — A Theoretical Approach (p. 90)", "14.2 Summary (p. 105)"), listOf("14.1"))
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
