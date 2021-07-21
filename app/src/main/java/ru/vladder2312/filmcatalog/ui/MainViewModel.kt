package ru.vladder2312.filmcatalog.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.vladder2312.filmcatalog.App
import ru.vladder2312.filmcatalog.data.MovieRepository
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject lateinit var movieRepository: MovieRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    val movies: MutableLiveData<List<Movie>>
    val errorMessage: MutableLiveData<String>

    init {
        App.appComponent.inject(this)
        movies = movieRepository.movies
        errorMessage = movieRepository.errorMessage
    }

    fun getMovies() {
        movieRepository.getMovies()
    }

    fun searchMovies(text: String) {
        movieRepository.searchMovies(text)
    }

    fun saveLikeState(movie: Movie) {
        if (movie.isFavourite) {
            sharedPreferences.edit().putBoolean(movie.id.toString(), movie.isFavourite).apply()
        } else {
            sharedPreferences.edit().remove(movie.id.toString()).apply()
        }
    }
}