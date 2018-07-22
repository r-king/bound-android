package io.richardking.bound.vo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.TypeConverters
import io.richardking.bound.db.BoundTypeConverters
import com.google.gson.annotations.SerializedName

@Entity(
        indices = [
            (Index("id"))],
        primaryKeys = ["id"]
)
@TypeConverters(BoundTypeConverters::class)
data class Book(
        val id: String,
        @field:SerializedName("volumeInfo")
        @field:Embedded(prefix = "volumeInfo_")
        val volumeInfo: VolumeInfo,
        @field:SerializedName("searchInfo")
        @field:Embedded(prefix = "searchInfo_")
        val searchInfo: SearchInfo?,
        val inLibrary: Boolean = false,
        val wishList: Boolean = false,
        val favourite: Boolean = false
) {

    data class VolumeInfo(
            @field:SerializedName("title")
            val title: String,
            @field:SerializedName("description")
            val description: String?,
            @field:SerializedName("authors")
            val authors: List<String>?,
            @field:SerializedName("publishedDate")
            val publishedDate: String?,
            @field:SerializedName("publisher")
            val publisher: String?,
            @field:SerializedName("pageCount")
            val pageCount: Int?,
            @field:SerializedName("averageRating")
            val averageRating: Double?,
            @field:SerializedName("ratingsCount")
            val ratingsCount: Int?,
            @field:SerializedName("language")
            val language: String?,
            @field:SerializedName("imageLinks")
            @field:Embedded(prefix = "imageLinks_")
            val imageLinks: ImageLinks?
    ) {
        data class ImageLinks(
                @field:SerializedName("smallThumbnail")
                val smallThumbnail: String?,
                @field:SerializedName("thumbnail")
                val thumbnail: String?
        )
    }

    data class SearchInfo(
            @field:SerializedName("textSnippet")
            val textSnippet: String?
    )
}