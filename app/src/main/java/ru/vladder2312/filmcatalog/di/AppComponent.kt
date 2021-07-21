package ru.vladder2312.filmcatalog.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import ru.vladder2312.filmcatalog.data.MovieRepository
import ru.vladder2312.filmcatalog.ui.MainViewModel
import javax.inject.Singleton

/**
 * Компонент приложения
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun provideContext(): Context
    fun provideMovieRepository(): MovieRepository
    fun provideSharedPreferences(): SharedPreferences

    fun inject(mainViewModel: MainViewModel)
}