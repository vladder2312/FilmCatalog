package ru.vladder2312.filmcatalog.data

import io.reactivex.Single
import ru.vladder2312.filmcatalog.domain.Movie
import javax.inject.Inject

/**
 * Репозиторий фильмов
 */
class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val spRepository: SharedPreferencesRepository
) {

    /**
     * Получение списка фильмов
     *
     * @return список фильмов обёрнутый в Single
     */
    fun getMovies() : Single<List<Movie>> = movieService.getMovies()
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

    /**
     * Получение отфильтрованого списка фильмов
     *
     * @param text текст для поиска фильмов по названию или описанию
     * @return список фильмов обёрнутый в обозреваемый Single объект
     */
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