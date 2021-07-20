package ru.vladder2312.filmcatalog.ui

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.squareup.picasso.Picasso
import ru.surfstudio.android.easyadapter.controller.BindableItemController
import ru.surfstudio.android.easyadapter.holder.BindableViewHolder
import ru.vladder2312.filmcatalog.R
import ru.vladder2312.filmcatalog.domain.Movie

class MoviesController(
    private val onClickListener: (Movie) -> Unit
) : BindableItemController<Movie, MoviesController.MovieHolder>() {

    inner class MovieHolder(parent: ViewGroup) :
        BindableViewHolder<Movie>(parent, R.layout.item_movie) {
        private val card: CardView = itemView.findViewById(R.id.item_movie)
        private val image: ImageView = itemView.findViewById(R.id.movie_cover)
        private val title: TextView = itemView.findViewById(R.id.movie_title)
        private val overview: TextView = itemView.findViewById(R.id.movie_overview)
        private val release: TextView = itemView.findViewById(R.id.movie_release_date)
        private val like: ImageView = itemView.findViewById(R.id.movie_like)
        private lateinit var movie: Movie

        init {
            card.setOnClickListener { onClickListener(movie) }
        }

        override fun bind(data: Movie) {
            movie = data
            title.text = movie.title
            overview.text = movie.overview
            release.text = movie.releaseDate
            if (movie.isFavourite) {
                like.setImageResource(R.drawable.ic_like_checked)
            }
            if (movie.cover != null) {
                Picasso.get().load("https://image.tmdb.org/t/p/w780"+movie.cover).into(image)
            }
        }
    }

    override fun createViewHolder(parent: ViewGroup) = MovieHolder(parent)

    override fun getItemId(data: Movie) = data.id.toString()
}