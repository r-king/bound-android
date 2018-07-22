package io.richardking.bound.respository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import io.richardking.bound.api.GoogleBooksService
import io.richardking.bound.AppExecutors
import io.richardking.bound.vo.Book
import io.richardking.bound.vo.BookSearchResult
import io.richardking.bound.vo.Resource
import io.richardking.bound.api.ApiSuccessResponse
import io.richardking.bound.api.BookSearchResponse
import io.richardking.bound.db.BoundDb
import io.richardking.bound.db.BookDao
import io.richardking.bound.utilities.AbsentLiveData
import io.richardking.bound.utilities.RateLimiter
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
        private val appExecutors: AppExecutors,
        private val db: BoundDb,
        private val bookDao: BookDao,
        private val googleBooksService: GoogleBooksService
) {
    private val bookListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun search(query: String): LiveData<Resource<List<Book>>> {
        return object : NetworkBoundResource<List<Book>, BookSearchResponse>(appExecutors) {

            override fun saveCallResult(item: BookSearchResponse) {
                val bookIds = item.items.map { it.id }
                val repoSearchResult = BookSearchResult(
                        query = query,
                        repoIds = bookIds,
                        totalCount = item.total,
                        next = item.nextPage
                )
                db.beginTransaction()
                try {
                    bookDao.insertBooks(item.items)
                    bookDao.insert(repoSearchResult)
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }

            override fun shouldFetch(data: List<Book>?) = data == null

            override fun loadFromDb(): LiveData<List<Book>> {
                return Transformations.switchMap(bookDao.search(query)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        bookDao.loadOrdered(searchData.repoIds)
                    }
                }
            }

            override fun createCall() = googleBooksService.searchBooks(query)

            override fun processResponse(response: ApiSuccessResponse<BookSearchResponse>)
                    : BookSearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
                query = query,
                googleBooksService = googleBooksService,
                db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun loadBook(id: String): LiveData<Book> {
        return bookDao.loadBook(id)
    }

    fun updateLibrary(inLibrary: Boolean, id: String) {
        appExecutors.diskIO().execute {
            db.beginTransaction()
            try {
                if (inLibrary) {
                    bookDao.removeFromLibrary(id)
                } else {
                    bookDao.addToLibrary(id)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun updateWishlist(wishlist: Boolean, id: String) {
        appExecutors.diskIO().execute {
            db.beginTransaction()
            try {
                if (wishlist) {
                    bookDao.removeFromWishlist(id)
                } else {
                    bookDao.addToWishlist(id)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun updateFavourite(favourite: Boolean, id: String) {
        appExecutors.diskIO().execute {
            db.beginTransaction()
            try {
                if (favourite) {
                    bookDao.removeFromFavourites(id)
                } else {
                    bookDao.addToFavourites(id)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun loadLibrary(): LiveData<List<Book>> {
        return bookDao.loadLibrary()
    }

    fun loadWishlist(): LiveData<List<Book>> {
        return bookDao.loadWishlist()
    }

    fun loadFavourites(): LiveData<List<Book>> {
        return bookDao.loadFavourites()
    }
}