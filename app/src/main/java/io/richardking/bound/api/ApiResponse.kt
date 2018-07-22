package io.richardking.bound.api

import android.util.Log
import retrofit2.Response
import java.util.regex.Pattern

/**
* Common class used by API responses.
* @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()

                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                            body = body,
                            url = response.raw().request().url().toString()
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
        val body: T,
        val url: String
) : ApiResponse<T>() {

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        url.let { next ->
            val matcher = INDEX_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                10
            } else {
                try {
                    Integer.parseInt(matcher.group(1)) + 10
                } catch (ex: NumberFormatException) {
                    Log.w("parse_error", next)
                    null
                }
            }
        }
    }

    companion object {
        private val INDEX_PATTERN = Pattern.compile("\\bstartIndex=(\\d+)")
    }
}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()