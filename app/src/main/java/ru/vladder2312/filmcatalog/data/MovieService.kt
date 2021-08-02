package ru.vladder2312.filmcatalog.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vladder2312.filmcatalog.data.response.MoviesResponse

/**
 * Сервис для получения фильмов
 */
interface MovieService {

    @GET(ConstantsAPI.GET_MOVIES)
    fun getMovies(): Single<MoviesResponse>

    @GET(ConstantsAPI.SEARCH_MOVIES)
    fun searchMovies(
        @Query("query") text: String
    ): Single<MoviesResponse>
}