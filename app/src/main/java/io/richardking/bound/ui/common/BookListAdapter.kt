package io.richardking.bound.ui.common

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import io.richardking.bound.vo.Book
import io.richardking.bound.R
import io.richardking.bound.AppExecutors
import io.richardking.bound.databinding.ItemBookBinding

/**
 * A RecyclerView adapter for [Book] class
 */
class BookListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val bookClickCallback: ((Book) -> Unit)?
) : DataBoundListAdapter<Book, ItemBookBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.volumeInfo.title == newItem.volumeInfo.title
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
) {

    override fun createBinding(parent: ViewGroup): ItemBookBinding {

        val binding = DataBindingUtil.inflate<ItemBookBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_book,
                parent,
                false,
                dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.book?.let {
                bookClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ItemBookBinding, item: Book) {
        binding.book = item
    }
}
