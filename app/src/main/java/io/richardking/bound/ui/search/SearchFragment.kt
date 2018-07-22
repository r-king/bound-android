package io.richardking.bound.ui.search


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import io.richardking.bound.AppExecutors
import io.richardking.bound.MainActivity
import io.richardking.bound.R
import io.richardking.bound.ui.common.BookListAdapter
import io.richardking.bound.binding.FragmentDataBindingComponent
import io.richardking.bound.databinding.FragmentSearchBinding
import io.richardking.bound.di.Injectable
import io.richardking.bound.ui.common.RetryCallback
import io.richardking.bound.utilities.autoCleared
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject


/**
 * A fragment representing the book search function screen
 */
class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentSearchBinding>()

    var adapter by autoCleared<BookListAdapter>()

    lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_search,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        (activity as MainActivity).setNavigationVisibility(false)
        showKeyboard()
    }


    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        dismissKeyboard(view!!.applicationWindowToken)
        (activity as MainActivity).setNavigationVisibility(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        searchViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchViewModel::class.java)
        initRecyclerView()
        val rvAdapter = BookListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors
        ) { book ->
            navController().navigate(
                    SearchFragmentDirections.showBook(book.id)
            )
        }

        binding.bookList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()

        btn_search_back.setOnClickListener {
            navController().navigate(R.id.nav_graph)
        }

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }

    /**
    *
    */
    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }

        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        // Dismiss keyboard
        dismissKeyboard(v.windowToken)

        binding.query = query
        searchViewModel.setQuery(query)
    }

    private fun initRecyclerView() {

        binding.bookList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        searchViewModel.results.observe(this, Observer { result ->
            binding.searchResource = result
            binding.resultCount = result?.data?.size ?: 0
            adapter.submitList(result?.data)
        })

        searchViewModel.loadMoreStatus.observe(this, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showKeyboard() {
        binding.input.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.input, InputMethodManager.SHOW_IMPLICIT)
    }


    fun navController() = findNavController()
}
