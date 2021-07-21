package ru.vladder2312.filmcatalog.data

import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val spRepository: SharedPreferencesRepository
) {

    fun getMovies() = movieService.getMovies()
        .map { movies ->
            movies.results.map { movie ->
                movie.transform()
            }
        }
        .map { movies ->
            movies.map { movie ->
                if (spRepository.isMovieFavourite(movie.id)) {
                    movie.isFavourite = true
                }
                movie
            }
        }

    fun searchMovies(text: String) = movieService.searchMovies(text)
        .map {
            it.results.map { m ->
                m.transform()
            }
        }
        .map { movies ->
            movies.map { movie ->
                if (spRepository.isMovieFavourite(movie.id)) {
                    movie.isFavourite = true
                }
                movie
            }
        }

}