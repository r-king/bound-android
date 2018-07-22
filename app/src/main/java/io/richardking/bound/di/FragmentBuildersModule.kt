package io.richardking.bound.di

import io.richardking.bound.ui.book.BookFragment
import io.richardking.bound.ui.favourites.FavouritesFragment
import io.richardking.bound.bound.ui.library.LibraryFragment
import io.richardking.bound.ui.search.SearchFragment
import io.richardking.bound.ui.wishlist.WishlistFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeBookFragment(): BookFragment

    @ContributesAndroidInjector
    abstract fun contributeLibraryFragment(): LibraryFragment

    @ContributesAndroidInjector
    abstract fun contributeWishlistFragment(): WishlistFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragment(): FavouritesFragment
}
