package io.richardking.bound.respository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.richardking.bound.vo.BookSearchResult
import io.richardking.bound.vo.Resource
import io.richardking.bound.api.*
import io.richardking.bound.db.BoundDb
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextSearchPageTask constructor(
        private val query: String,
        private val googleBooksService: GoogleBooksService,
        private val db: BoundDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {

        val current = db.bookDao().findSearchResult(query)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next

        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response = googleBooksService.searchBooks(query, nextPage).execute()
            val apiResponse = ApiResponse.create(response)
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    // we merge all book ids into 1 list so that it is easier to fetch the
                    // result list.
                    val ids = arrayListOf<String>()
                    ids.addAll(current.repoIds)

                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = BookSearchResult(
                            query, ids,
                            apiResponse.body.total, apiResponse.nextPage
                    )
                    try {
                        db.beginTransaction()
                        db.bookDao().insert(merged)
                        db.bookDao().insertBooks(apiResponse.body.items)
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                    Resource.success(apiResponse.nextPage != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
