package ru.vladder2312.filmcatalog.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.vladder2312.filmcatalog.R
import ru.vladder2312.filmcatalog.data.repositories.SharedPreferencesRepository
import javax.inject.Singleton

/**
 * Модуль приложения
 */
@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(context.getString(R.string.favourites_shared_pref), Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(): SharedPreferencesRepository {
        return SharedPreferencesRepository(provideSharedPreferences())
    }
}