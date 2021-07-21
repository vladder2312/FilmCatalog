package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.vladder2312.filmcatalog.R
import java.util.concurrent.TimeUnit

/**
 * Главная активность приложения
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val movieAdapter = EasyAdapter()
    private val movieController = MoviesController(
        {
            Toast.makeText(this, it.title, Toast.LENGTH_LONG).show()
        },
        {
            viewModel.saveLikeState(it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews()
        initRecycler()
        initSearchView()
        initData()
        observeData()
    }

    private fun initViews() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        swipe_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue))
        swipe_refresh.setOnRefreshListener {
            initData()
            progress_indicator.visibility = View.VISIBLE
        }
        refresh_button.setOnClickListener {
            initData()
            refresh_button.visibility = View.INVISIBLE
            progress_indicator.visibility = View.VISIBLE
        }
    }

    private fun initSearchView() {
        val disposable = Observable.create<String> {
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
        }
            .filter { it.isNotEmpty() }
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe {
                viewModel.searchMovies(it)
            }
    }

    private fun initRecycler() {
        recycler_movie.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recycler_movie.adapter = movieAdapter
    }

    private fun initData() {
        if (search_view.query.isNotEmpty()) {
            viewModel.searchMovies(search_view.query.toString())
        } else {
            viewModel.getMovies()
        }
    }

    private fun observeData() {
        viewModel.movies.observe(this) {
            movieAdapter.setData(it, movieController)
            hideLoadingBars()
            query_error_layout.visibility = View.INVISIBLE
            if (it.isEmpty()) {
                not_found_layout.visibility = View.VISIBLE
                not_found_text.text = getString(R.string.not_found, search_view.query)
//                "По запросу \"${search_view.query}\" ничего не найдено"
            } else {
                not_found_layout.visibility = View.INVISIBLE
            }
        }
        viewModel.errorMessage.observe(this) {
            if (movieAdapter.itemCount == 0) {
                query_error_layout.visibility = View.VISIBLE
                query_error_text.text = getString(R.string.query_error)
            } else {
                query_error_layout.visibility = View.INVISIBLE
            }
            hideLoadingBars()
            showSnackBar(getString(R.string.connection_error))
        }
    }

    private fun hideLoadingBars() {
        refresh_button.visibility = View.VISIBLE
        progress_indicator.visibility = View.INVISIBLE
        progress_bar.visibility = View.INVISIBLE
        swipe_refresh.isRefreshing = false
    }

    private fun showSnackBar(string: String) {
        Snackbar.make(main_view, string, Snackbar.LENGTH_SHORT).show()
    }
}