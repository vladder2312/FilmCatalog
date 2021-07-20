package ru.vladder2312.filmcatalog.domain

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String?,
    val cover: String?,
    var isFavourite: Boolean
)