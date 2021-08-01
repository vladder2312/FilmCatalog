package ru.vladder2312.filmcatalog.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Репозиторий внутренней памяти устройства
 */
class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    /**
     * Проверка является ли фильм избранным пользователем ранее
     *
     * @param movieId ID фильма
     * @return true - избранный, false - не избранный
     */
    fun isMovieFavourite(movieId: Int) : Boolean{
        return sharedPreferences.getBoolean(movieId.toString(), false)
    }

    /**
     * Устоновка значения избранности фильма
     *
     * @param movieId ID фильма
     * @param isFavourite является ли фильм избранным
     */
    fun setMovieFavourite(movieId: Int, isFavourite: Boolean) {
        if(isFavourite) {
            sharedPreferences.edit().putBoolean(movieId.toString(), true).apply()
        } else {
            sharedPreferences.edit().remove(movieId.toString()).apply()
        }
    }
}