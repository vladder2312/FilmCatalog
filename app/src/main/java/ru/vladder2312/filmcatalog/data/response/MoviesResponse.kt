package ru.vladder2312.filmcatalog.data.response

import com.google.gson.annotations.SerializedName

/**
 * Класс Фильмы, получаемый с API
 */
data class MoviesResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieResponse>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)