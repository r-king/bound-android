package io.richardking.bound.ui.common

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import io.richardking.bound.AppExecutors
import io.richardking.bound.R
import io.richardking.bound.vo.Book
import io.richardking.bound.databinding.ItemBookCompactBinding

/**
 * A RecyclerView adapter for [Book] class
 * Used for compact view of book
 */
class CompactBookListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val bookClickCallback: ((Book) -> Unit)?
) : DataBoundListAdapter<Book, ItemBookCompactBinding>(
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

    override fun createBinding(parent: ViewGroup): ItemBookCompactBinding {

        val binding = DataBindingUtil.inflate<ItemBookCompactBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_book_compact,
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

    override fun bind(binding: ItemBookCompactBinding, item: Book) {
        binding.book = item
    }
}