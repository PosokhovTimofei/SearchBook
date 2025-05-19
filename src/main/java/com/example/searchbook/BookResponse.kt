data class OpenLibraryResponse(
    val docs: List<BookDoc>?
)

data class BookDoc(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val language: List<String>?,
    val cover_i: Int?,
    val key: String?
)


data class BookDetails(
    val title: String?,
    val description: Any?, // Может быть строка или объект
    val covers: List<Int>?,
    val subject_places: List<String>?,
    val subject_people: List<String>?,
    val subject_times: List<String>?,
    val subjects: List<String>?
)


