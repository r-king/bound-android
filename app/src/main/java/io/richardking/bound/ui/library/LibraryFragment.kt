package io.richardking.bound.bound.ui.library


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.richardking.bound.AppExecutors
import io.richardking.bound.binding.FragmentDataBindingComponent
import io.richardking.bound.R
import io.richardking.bound.ui.common.CompactBookListAdapter
import io.richardking.bound.databinding.FragmentLibraryBinding
import io.richardking.bound.utilities.autoCleared
import io.richardking.bound.di.Injectable
import io.richardking.bound.ui.library.LibraryViewModel
import javax.inject.Inject


/**
 * A fragment representing books in library screen
 */
class LibraryFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentLibraryBinding>()

    var adapter by autoCleared<CompactBookListAdapter>()

    lateinit var libraryViewModel: LibraryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_library,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        libraryViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(LibraryViewModel::class.java)
        initRecyclerView()
        val rvAdapter = CompactBookListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors
        ) { book ->
            navController().navigate(
                    LibraryFragmentDirections.showBook(book.id)
            )
        }

        binding.libraryBookList.adapter = rvAdapter
        adapter = rvAdapter
    }

    private fun initRecyclerView() {

        libraryViewModel.getLibraryBooks().observe(this, Observer { result ->
            binding.resultCount = result?.size ?: 0
            adapter.submitList(result)
        })
    }

    fun navController() = findNavController()


}
