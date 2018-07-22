package io.richardking.bound.ui.wishlist

import android.arch.lifecycle.ViewModel
import io.richardking.bound.respository.BookRepository
import javax.inject.Inject

/**
 * The ViewModel used in [WishlistFragment].
 */
class WishlistViewModel @Inject constructor(booksRepository: BookRepository) : ViewModel() {

    private val wishlistBooks = booksRepository.loadWishlist()

    fun getWishlistBooks() = wishlistBooks
}