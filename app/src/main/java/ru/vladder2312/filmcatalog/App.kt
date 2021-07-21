package ru.vladder2312.filmcatalog

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import ru.vladder2312.filmcatalog.di.AppComponent
import ru.vladder2312.filmcatalog.di.AppModule
import ru.vladder2312.filmcatalog.di.DaggerAppComponent

/**
 * Класс инициализации приложения
 */
class App : Application() {

    companion object {
        lateinit var appComponent : AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler {
            if(it is UndeliverableException) {
                Log.e("RX", "Global error")
            }
        }
        appComponent = DaggerAppComponent.builder().appModule(AppModule(applicationContext)).build()
    }
}