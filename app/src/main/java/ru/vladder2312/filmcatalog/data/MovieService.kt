package ru.vladder2312.filmcatalog.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") api_key: String = "6ccd72a2a8fc239b13f209408fc31c33",
        @Query("language") language: String = "ru-RU"
    ): Single<MoviesResponse>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") text: String,
        @Query("api_key") api_key: String = "6ccd72a2a8fc239b13f209408fc31c33",
        @Query("language") language: String = "ru-RU"
    ): Single<MoviesResponse>
}