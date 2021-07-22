package ru.vladder2312.filmcatalog.ui

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.vladder2312.filmcatalog.App
import ru.vladder2312.filmcatalog.data.repositories.MovieRepository
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

/**
 * Модель главного представления
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject lateinit var movieRepository: MovieRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    val movies = MutableLiveData<List<Movie>>()
    val foundMovies = MutableLiveData<List<Movie>>()
    val errorMessage = MutableLiveData<String>()
    lateinit var loadDisposable : Disposable
    lateinit var searchDisposable : Disposable

    init {
        App.appComponent.inject(this)
    }

    fun loadMovies() {
        loadDisposable = movieRepository.getMovies()
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
        searchDisposable = movieRepository.searchMovies(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    foundMovies.postValue(it)
                },
                {
                    errorMessage.postValue(it.localizedMessage)
                    Log.d("TAG", it.toString())
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

    override fun onCleared() {
        loadDisposable.dispose()
        searchDisposable.dispose()
        super.onCleared()
    }
}