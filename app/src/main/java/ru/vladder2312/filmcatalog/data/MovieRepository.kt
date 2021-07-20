package ru.vladder2312.filmcatalog.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieService: MovieService
) {
    val response = MutableLiveData<MoviesResponse>()

    fun getMovies() {
        val disposable = movieService.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    response.postValue(it)
                    Log.d("MYTAG", it.toString())
                },
                {
                    Log.e("MYTAG", it.toString())
                }
            )
    }
}