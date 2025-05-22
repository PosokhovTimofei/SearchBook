import kotlinx.serialization.Serializable

data class OpenLibraryResponse(
    val docs: List<BookDoc>?
)

data class BookDoc(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val language: List<String>?,
    val cover_i: Int?,
    val key: String?,
    val isbn: List<String>? = null,
    var translatedTitle: String? = null,
    var isFavorite: Boolean = false
)


// Детальная информация о книге (из works/OLxxxx.json)
data class BookDetails(
    val title: String?,
    val description: Any?,
    val covers: List<Int>?,
    val subject_places: List<String>?,
    val subject_people: List<String>?,
    val subject_times: List<String>?,
    val subjects: List<String>?,
    val excerpts: List<Excerpt>? = null,
    val links: List<Link>? = null,
    val authors: List<AuthorWrapper>? = null
)

// Обёртка для автора, как в JSON
data class AuthorWrapper(
    val author: Author?
)

// Собственно автор с ключом (и возможно другими полями)
data class Author(
    val key: String?
)


data class Excerpt(
    val excerpt: String?,
    val comment: String?
)

data class Link(
    val title: String?,
    val url: String?
)


@Serializable
data class FavoriteBookRequest(
    val key: String,
    val title: String?,
    val author: String?,
    val cover_i: Int?,
    val userId: Int
)


