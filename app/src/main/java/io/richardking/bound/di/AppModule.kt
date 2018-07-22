package io.richardking.bound.di

import android.app.Application
import android.arch.persistence.room.Room
import io.richardking.bound.api.GoogleBooksService
import io.richardking.bound.db.BookDao
import io.richardking.bound.db.BoundDb
import io.richardking.bound.utilities.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideGoogleBooksService(): GoogleBooksService {
        return Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(GoogleBooksService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): BoundDb {
        return Room
                .databaseBuilder(app, BoundDb::class.java, "bound.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideBookDao(db: BoundDb): BookDao {
        return db.bookDao()
    }
}