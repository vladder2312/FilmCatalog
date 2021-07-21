package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        swipe_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue))

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initRecycler()
        initSearchView()
        getData()
        observeData()

        swipe_refresh.setOnRefreshListener {
            getData()
            progress_indicator.visibility = View.VISIBLE
        }
        refresh_button.setOnClickListener {
            getData()
            progress_indicator.visibility = View.VISIBLE
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
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe {
                mainViewModel.searchMovies(it)
            }
    }

    private fun initRecycler() {
        recycler_movie.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recycler_movie.adapter = movieAdapter
    }

    private fun getData() {
        if (search_view.query.isNotEmpty()) {
            mainViewModel.searchMovies(search_view.query.toString())
        } else {
            mainViewModel.getMovies()
        }
    }

    private fun observeData() {
        mainViewModel.data.observe(this) {
            movieAdapter.setData(it, movieController)
            swipe_refresh.isRefreshing = false
            progress_indicator.visibility = View.INVISIBLE
            progress_bar.visibility = View.INVISIBLE
            query_error_layout.visibility = View.INVISIBLE
            if (it.isEmpty()) {
                not_found_layout.visibility = View.VISIBLE
                not_found_text.text = "По запросу \"${search_view.query}\" ничего не найдено"
            } else {
                not_found_layout.visibility = View.INVISIBLE
            }
        }
        mainViewModel.errorMessage.observe(this) {
            movieAdapter.setData(listOf(), movieController)
            swipe_refresh.isRefreshing = false
            progress_indicator.visibility = View.INVISIBLE
            progress_bar.visibility = View.INVISIBLE
            query_error_layout.visibility = View.VISIBLE
            query_error_text.text = getString(R.string.query_error)
        }
    }
}