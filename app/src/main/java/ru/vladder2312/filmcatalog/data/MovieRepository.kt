package ru.vladder2312.filmcatalog.data

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val sharedPreferences: SharedPreferences
) {
    val movies = MutableLiveData<List<Movie>>()
    val errorMessage = MutableLiveData<String>()

    fun getMovies() {
        val disposable = movieService.getMovies()
            .map { movies ->
                movies.results.map { movie ->
                    movie.transform()
                }
            }
            .map { movies ->
                movies.map { movie ->
                    if (sharedPreferences.getBoolean(movie.id.toString(), false)) {
                        movie.isFavourite = true
                    }
                    movie
                }
            }
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
        val disposable = movieService.searchMovies(text)
            .map {
                it.results.map { m ->
                    m.transform()
                }
            }
            .map {
                it.map { m ->
                    if (sharedPreferences.getBoolean(m.id.toString(), false)) {
                        m.isFavourite = true
                    }
                    m
                }
            }
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
}