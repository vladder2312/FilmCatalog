package ru.vladder2312.filmcatalog.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
    private val _state = MutableLiveData<MainViewState>()
    val state: LiveData<MainViewState>
        get() = _state

    init {
        App.appComponent.inject(this)
        loadMovies()
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun saveLikeStateInSharedPref(movie: Movie) {
        spRepository.setMovieFavourite(movie.id, movie.isFavourite)
    }

    fun loadMovies() {
        disposables.add(movieRepository.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onLoading() }
            .subscribe(
                { movies -> onSuccess(movies) },
                {
                    onError()
                    Log.e("TAG", it.toString())
                }
            )
        )
    }

    fun searchMovies(text: String) {
        disposables.add(movieRepository.searchMovies(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onLoading() }
            .subscribe(
                { movies -> onSuccess(movies) },
                {
                    onError()
                    Log.e("TAG", it.toString())
                }
            )
        )
    }

    private fun onLoading() {
        _state.postValue(MainViewState.LoadingViewState)
    }

    private fun onSuccess(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            setLikeStateFromSharedPref(movies)
            _state.postValue(MainViewState.MoviesViewState(movies))
        } else {
            _state.postValue(MainViewState.NotFoundViewState)
        }
    }

    private fun onError() {
        _state.postValue(MainViewState.QueryErrorViewState)
    }

    private fun setLikeStateFromSharedPref(movies: List<Movie>) {
        movies.forEach { movie ->
            movie.isFavourite = spRepository.isMovieFavourite(movie.id)
        }
    }
}