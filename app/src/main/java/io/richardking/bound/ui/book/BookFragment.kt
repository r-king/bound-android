package io.richardking.bound.ui.book

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import io.richardking.bound.R
import io.richardking.bound.binding.FragmentDataBindingComponent
import io.richardking.bound.databinding.FragmentBookBinding
import io.richardking.bound.utilities.autoCleared
import io.richardking.bound.di.Injectable
import javax.inject.Inject

/**
 * A Fragment representing Book information screen
 */
class BookFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentBookBinding>()

    lateinit var bookViewModel: BookViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_book,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        bookViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BookViewModel::class.java)

        bookViewModel.setId(BookFragmentArgs.fromBundle(arguments).id)

        val book = bookViewModel.book
        book.observe(this, Observer { resource ->
            binding.book = resource
        })

        binding.btnAddToLibrary.setOnClickListener(libraryClickListener)
        binding.btnRemoveFromLibrary.setOnClickListener(libraryClickListener)

        binding.btnAddToWishlist.setOnClickListener(wishlistClickListener)
        binding.btnRemoveFromWishlist.setOnClickListener(wishlistClickListener)

        binding.btnAddToFavourites.setOnClickListener(favouriteClickListener)
        binding.btnRemoveFromFavourites.setOnClickListener(favouriteClickListener)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        // hide search option in toolbar
        menu!!.findItem(R.id.searchFragment).isVisible = false
    }

    private val libraryClickListener = View.OnClickListener {
        bookViewModel.updateLibrary()
    }

    private val wishlistClickListener = View.OnClickListener {
        bookViewModel.updateWishlist()
    }

    private val favouriteClickListener = View.OnClickListener {
        bookViewModel.updateFavourite()
    }

}