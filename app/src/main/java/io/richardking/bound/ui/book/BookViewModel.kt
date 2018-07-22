package io.richardking.bound.ui.book

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.richardking.bound.respository.BookRepository
import io.richardking.bound.utilities.AbsentLiveData
import io.richardking.bound.vo.Book
import javax.inject.Inject

/**
 * The ViewModel used in [BookFragment].
 */
class BookViewModel @Inject constructor(private val bookRepository: BookRepository) : ViewModel() {

    private val _id = MutableLiveData<String>()

    val book: LiveData<Book> = Transformations
            .switchMap(_id) { id ->
                if (id == null) {
                    AbsentLiveData.create()
                } else {
                    bookRepository.loadBook(id)
                }
            }


    fun setId(id: String?) {
        if (_id.value != id) {
            _id.value = id
        }
    }

    fun updateLibrary() {
        bookRepository.updateLibrary(book.value!!.inLibrary, _id.value.toString())
    }

    fun updateWishlist() {
        bookRepository.updateWishlist(book.value!!.wishList, _id.value.toString())
    }

    fun updateFavourite() {
        bookRepository.updateFavourite(book.value!!.favourite, _id.value.toString())
    }
}