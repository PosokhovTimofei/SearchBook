import com.example.searchbook.data.model.BookDoc
import kotlinx.serialization.Serializable

data class OpenLibraryResponse(
    val docs: List<BookDoc>?
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


