package io.richardking.bound.ui.library

import android.arch.lifecycle.ViewModel
import io.richardking.bound.respository.BookRepository
import javax.inject.Inject

/**
 * The ViewModel used in [LibraryFragment].
 */
class LibraryViewModel @Inject constructor(booksRepository: BookRepository) : ViewModel() {

    private val libraryBooks = booksRepository.loadLibrary()

    fun getLibraryBooks() = libraryBooks
}
