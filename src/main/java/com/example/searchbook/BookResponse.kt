import com.example.searchbook.data.model.BookDoc

data class OpenLibraryResponse(
    val docs: List<BookDoc>?
)

data class AuthorWrapper(
    val author: Author?
)

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





