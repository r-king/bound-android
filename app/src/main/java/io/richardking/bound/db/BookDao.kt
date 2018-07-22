package io.richardking.bound.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.*
import android.util.ArrayMap
import io.richardking.bound.vo.Book
import io.richardking.bound.vo.BookSearchResult
import java.util.*

/**
 * Interface for database access on Book related operations.
 */
@Dao
abstract class BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(vararg books: Book)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertBooks(repositories: List<Book>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createBookIfNotExists(book: Book): Long

    @Query(
            """
        SELECT * FROM Book
        ORDER BY volumeInfo_title DESC"""
    )
    abstract fun loadBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE `id` = :id")
    abstract fun loadBook(id: String): LiveData<Book>

    @Query("UPDATE Book SET inLibrary = 1 WHERE `id` = :id")
    abstract fun addToLibrary(id: String)

    @Query("UPDATE Book SET inLibrary = 0 WHERE `id` = :id")
    abstract fun removeFromLibrary(id: String)

    @Query("UPDATE Book SET wishlist = 1 WHERE `id` = :id")
    abstract fun addToWishlist(id: String)

    @Query("UPDATE Book SET wishlist = 0 WHERE `id` = :id")
    abstract fun removeFromWishlist(id: String)

    @Query("UPDATE Book SET favourite = 1 WHERE `id` = :id")
    abstract fun addToFavourites(id: String)

    @Query("UPDATE Book SET favourite = 0 WHERE `id` = :id")
    abstract fun removeFromFavourites(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: BookSearchResult)

    @Query("SELECT * FROM BookSearchResult WHERE `query` = :query")
    abstract fun search(query: String): LiveData<BookSearchResult>

    fun loadOrdered(bookIds: List<String>): LiveData<List<Book>> {
        val order = ArrayMap<String, Int>()
        bookIds.withIndex().forEach {
            order[it.value] = it.index
        }
        return Transformations.map(loadById(bookIds)) { books ->
            Collections.sort(books) { r1, r2 ->
                val pos1 = order[r1.id]
                val pos2 = order[r2.id]
                pos1!! - pos2!!
            }
            books
        }
    }

    @Query("SELECT * FROM Book WHERE id in (:bookIds)")
    protected abstract fun loadById(bookIds: List<String>): LiveData<List<Book>>

    @Query("SELECT * FROM BookSearchResult WHERE `query` = :query")
    abstract fun findSearchResult(query: String): BookSearchResult?

    @Query("SELECT * FROM Book WHERE inLibrary = 1")
    abstract fun loadLibrary(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE wishlist = 1")
    abstract fun loadWishlist(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE favourite = 1")
    abstract fun loadFavourites(): LiveData<List<Book>>

}