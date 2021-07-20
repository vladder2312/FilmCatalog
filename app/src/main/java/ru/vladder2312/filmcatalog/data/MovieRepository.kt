package ru.vladder2312.filmcatalog.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val sharedPreferences: SharedPreferences
) {
    val response = MutableLiveData<List<Movie>>()

    fun getMovies() {
        val disposable = movieService.getMovies()
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
                    response.postValue(it)
                },
                {
                    Log.e("MYTAG", it.toString())
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
                    response.postValue(it)
                },
                {
                    Log.e("MYTAG", it.toString())
                }
            )
    }
}