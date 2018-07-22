package io.richardking.bound.ui.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.richardking.bound.vo.Book
import io.richardking.bound.vo.Resource
import io.richardking.bound.respository.BookRepository
import io.richardking.bound.vo.Status
import io.richardking.bound.utilities.AbsentLiveData
import java.util.*
import javax.inject.Inject

/**
 * The ViewModel used in [SearchFragment].
 */
class SearchViewModel @Inject constructor(booksRepository: BookRepository) : ViewModel() {

    private val query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(booksRepository)

    val results: LiveData<Resource<List<Book>>> = Transformations
            .switchMap(query) { search ->
                if (search.isNullOrBlank()) {
                    AbsentLiveData.create()
                } else {
                    booksRepository.search(search)
                }
            }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState


    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        nextPageHandler.reset()
        query.value = input
    }

    fun loadNextPage() {
        query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun refresh() {
        query.value?.let {
            query.value = it
        }
    }


    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: BookRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(
                    isRunning = true,
                    errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = null
                                )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = result.message
                                )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                    isRunning = false,
                    errorMessage = null
            )
        }
    }
}