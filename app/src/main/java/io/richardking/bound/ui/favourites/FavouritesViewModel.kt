package io.richardking.bound.ui.favourites

import android.arch.lifecycle.ViewModel
import io.richardking.bound.respository.BookRepository
import javax.inject.Inject

/**
 * The ViewModel used in [FavouritesFragment].
 */
class FavouritesViewModel @Inject constructor(booksRepository: BookRepository) : ViewModel() {

    private val favoriteBooks = booksRepository.loadFavourites()

    fun getFavouriteBooks() = favoriteBooks
}