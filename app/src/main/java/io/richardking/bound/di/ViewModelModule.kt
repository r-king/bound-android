package io.richardking.bound.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.richardking.bound.ui.library.LibraryViewModel
import io.richardking.bound.ui.book.BookViewModel
import io.richardking.bound.ui.favourites.FavouritesViewModel
import io.richardking.bound.ui.search.SearchViewModel
import io.richardking.bound.ui.wishlist.WishlistViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.richardking.bound.viewmodels.BoundViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookViewModel::class)
    abstract fun bindBookViewModel(bookViewModel: BookViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LibraryViewModel::class)
    abstract fun bindLibraryViewModel(libraryViewModel: LibraryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WishlistViewModel::class)
    abstract fun bindWishlistViewModel(wishlistViewModel: WishlistViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel::class)
    abstract fun bindFavouritesViewModel(favouritesViewModel: FavouritesViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: BoundViewModelFactory): ViewModelProvider.Factory

}