package ru.vladder2312.filmcatalog.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.vladder2312.filmcatalog.App
import ru.vladder2312.filmcatalog.data.MovieRepository
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

/**
 * Модель главного представления
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject lateinit var movieRepository: MovieRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    val movies = MutableLiveData<List<Movie>>()
    val errorMessage = MutableLiveData<String>()

    init {
        App.appComponent.inject(this)
    }

    fun getMovies() {
        val disposable = movieRepository.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    movies.postValue(it)
                },
                {
                    errorMessage.postValue(it.localizedMessage)
                }
            )
    }

    fun searchMovies(text: String) {
        val disposable = movieRepository.searchMovies(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    movies.postValue(it)
                },
                {
                    errorMessage.postValue(it.localizedMessage)
                }
            )

    }

    fun saveLikeState(movie: Movie) {
        if (movie.isFavourite) {
            sharedPreferences.edit().putBoolean(movie.id.toString(), movie.isFavourite).apply()
        } else {
            sharedPreferences.edit().remove(movie.id.toString()).apply()
        }
    }
}