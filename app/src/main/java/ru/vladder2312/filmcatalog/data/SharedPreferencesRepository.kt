package ru.vladder2312.filmcatalog.data

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun isMovieFavourite(movieId: Int) : Boolean{
        return sharedPreferences.getBoolean(movieId.toString(), false)
    }

    fun setMovieFavourite(movieId: Int, isFavourite: Boolean) {
        if(isFavourite) {
            sharedPreferences.edit().putBoolean(movieId.toString(), true).apply()
        } else {
            sharedPreferences.edit().remove(movieId.toString()).apply()
        }
    }
}