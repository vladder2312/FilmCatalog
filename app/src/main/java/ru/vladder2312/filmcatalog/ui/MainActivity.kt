package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Predicate
import kotlinx.android.synthetic.main.activity_main.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.vladder2312.filmcatalog.R
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val movieAdapter = EasyAdapter()
    private val movieController = MoviesController(
        {
            Toast.makeText(this, it.title, Toast.LENGTH_LONG).show()
        },
        {
            mainViewModel.saveLikeState(it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initRecycler()
        initSearchView()
        observeData()

        mainViewModel.getMovies()

        swipe_refresh.setOnRefreshListener {
            search_view.setQuery("", false)
            mainViewModel.getMovies()
        }
    }

    private fun initSearchView() {
        val obs = Observable.create(ObservableOnSubscribe<String> {
            search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    it.onNext(query!!)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    it.onNext(newText!!)
                    return false
                }

            })
        })
            .filter { it.isNotEmpty() }
            .debounce(250, TimeUnit.MILLISECONDS)
            .distinct()
            .subscribe {
                mainViewModel.searchMovies(it)
            }
    }

    private fun initRecycler() {
        recycler_movie.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recycler_movie.adapter = movieAdapter
    }

    private fun observeData() {
        mainViewModel.data.observe(this) {
            movieAdapter.setData(it, movieController)
            swipe_refresh.isRefreshing = false
        }
    }
}