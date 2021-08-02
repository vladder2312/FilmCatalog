package ru.vladder2312.filmcatalog.ui

import ru.vladder2312.filmcatalog.domain.Movie

sealed class MainViewState {
    class MoviesViewState(val movies: List<Movie>) : MainViewState()
    object LoadingViewState : MainViewState()
    object NotFoundViewState : MainViewState()
    object QueryErrorViewState : MainViewState()
}
