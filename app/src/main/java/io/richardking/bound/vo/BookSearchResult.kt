package io.richardking.bound.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters
import io.richardking.bound.db.BoundTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(BoundTypeConverters::class)
data class BookSearchResult(
        val query: String,
        val repoIds: List<String>,
        val totalCount: Int,
        val next: Int?
)
