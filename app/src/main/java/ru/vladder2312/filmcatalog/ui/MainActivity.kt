package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.vladder2312.filmcatalog.R

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val movieAdapter = EasyAdapter()
    private val movieController = MoviesController {
        Toast.makeText(this, it.title, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initRecycler()
        observeData()

        mainViewModel.getMovies()
    }

    private fun initRecycler() {
        recycler_movie.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recycler_movie.adapter = movieAdapter
    }

    private fun observeData() {
        mainViewModel.data.observe(this) {
            movieAdapter.setData(it, movieController)
        }
    }
}