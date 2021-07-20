package ru.vladder2312.filmcatalog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.vladder2312.filmcatalog.App
import ru.vladder2312.filmcatalog.data.MovieRepository
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var movieRepository: MovieRepository
    val data : MutableLiveData<List<Movie>>

    init {
        App.appComponent.inject(this)
        data = movieRepository.response
    }

    fun getMovies() {
        movieRepository.getMovies()
    }

    fun searchMovies(text: String) {
        movieRepository.searchMovies(text)
    }

    fun saveLikeState(isFavourite: Boolean) {

    }
}