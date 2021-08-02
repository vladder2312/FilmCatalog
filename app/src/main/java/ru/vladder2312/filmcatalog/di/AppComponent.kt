package ru.vladder2312.filmcatalog.di

import dagger.Component
import ru.vladder2312.filmcatalog.ui.MainViewModel
import javax.inject.Singleton

/**
 * Компонент приложения
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(mainViewModel: MainViewModel)
}