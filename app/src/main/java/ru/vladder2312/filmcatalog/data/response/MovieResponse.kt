package ru.vladder2312.filmcatalog.data.response

import com.google.gson.annotations.SerializedName
import ru.vladder2312.filmcatalog.domain.Movie

/**
 * Модель фильма, получаемего с API
 */
data class MovieResponse(
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("genre_ids") val genreIds: MutableList<Int>,
    @SerializedName("id") val id: Int,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("title") val title: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val voteAverage: Float
) {

    /**
     * Трансформация в доменную модель
     */
    fun transform(): Movie {
        return Movie(id, title, overview, releaseDate, posterPath, false)
    }
}