package ru.vladder2312.filmcatalog.ui

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.vladder2312.filmcatalog.App
import ru.vladder2312.filmcatalog.data.repositories.MovieRepository
import ru.vladder2312.filmcatalog.data.repositories.SharedPreferencesRepository
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

/**
 * Модель главного представления
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieRepository: MovieRepository

    @Inject
    lateinit var spRepository: SharedPreferencesRepository

    private val disposables = CompositeDisposable()
    private val _movies = MutableLiveData<List<Movie>>()
    private val _foundMovies = MutableLiveData<List<Movie>>()
    private val _errorMessage = MutableLiveData<String>()
    val movies: LiveData<List<Movie>>
        get() = _movies
    val foundMovies: LiveData<List<Movie>>
        get() = _foundMovies
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        App.appComponent.inject(this)
    }

    fun loadMovies() {
        disposables.add(movieRepository.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _movies.postValue(it)
                },
                {
                    _errorMessage.postValue(it.localizedMessage)
                }
            )
        )
    }

    fun searchMovies(text: String) {
        disposables.add(movieRepository.searchMovies(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _foundMovies.postValue(it)
                },
                {
                    _errorMessage.postValue(it.localizedMessage)
                    Log.d("TAG", it.toString())
                }
            )
        )
    }

    fun saveLikeState(movie: Movie) {
        spRepository.setMovieFavourite(movie.id, movie.isFavourite)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}