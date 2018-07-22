package io.richardking.bound.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.richardking.bound.vo.Book
import io.richardking.bound.vo.BookSearchResult

/**
 * Main database description.
 */
@Database(
        entities = [
            Book::class,
            BookSearchResult::class],
        version = 1,
        exportSchema = false
)
abstract class BoundDb : RoomDatabase() {

    abstract fun bookDao(): BookDao
}