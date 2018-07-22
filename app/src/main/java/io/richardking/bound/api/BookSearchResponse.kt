package io.richardking.bound.api

import io.richardking.bound.vo.Book
import com.google.gson.annotations.SerializedName

/**
 * Simple object to hold search responses
 */
data class BookSearchResponse (
    @SerializedName("totalItems")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Book>
    ) {
        var nextPage: Int? = null
}