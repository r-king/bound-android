package io.richardking.bound.api

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Rest API access points
 */
interface GoogleBooksService {

    @GET("v1/volumes")
    fun searchBooks(@Query("q") query: String): LiveData<ApiResponse<BookSearchResponse>>

    @GET("v1/volumes")
    fun searchBooks(@Query("q") query: String, @Query("startIndex") page: Int): Call<BookSearchResponse>
}