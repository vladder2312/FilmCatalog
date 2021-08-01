package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_not_found.*
import kotlinx.android.synthetic.main.layout_query_error.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.vladder2312.filmcatalog.R
import ru.vladder2312.filmcatalog.domain.Movie
import java.util.concurrent.TimeUnit

/**
 * Главная активность приложения
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var viewModel: MainViewModel
    private val disposables = CompositeDisposable()
    private val movieAdapter = EasyAdapter()
    private val movieController = MoviesController(
        {
            Toast.makeText(this, it.title, Toast.LENGTH_LONG).show()
        },
        {
            viewModel.saveLikeStateInSharedPref(it)
        }
    )
    private val moviesObserver = Observer<List<Movie>> {
        movieAdapter.setData(it, movieController)
        hideLoadingBars()
        query_error_layout.visibility = View.INVISIBLE
        if (it.isEmpty()) {
            not_found_layout.visibility = View.VISIBLE
            not_found_text.text = getString(R.string.not_found, search_view.query)
        } else {
            not_found_layout.visibility = View.INVISIBLE
        }
    }
    private val errorsObserver = Observer<String> {
        if (movieAdapter.itemCount == 0) {
            query_error_layout.visibility = View.VISIBLE
            query_error_text.text = getString(R.string.query_error)
        } else {
            query_error_layout.visibility = View.INVISIBLE
        }
        hideLoadingBars()
        showSnackBar(getString(R.string.connection_error))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews()
        initRecycler()
        initSearchView()
        initData()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.movies.observe(this, moviesObserver)
        viewModel.foundMovies.observe(this, moviesObserver)
        viewModel.errorMessage.observe(this, errorsObserver)
    }

    private fun initViews() {
        swipe_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue))
        swipe_refresh.setOnRefreshListener {
            initData()
            progress_indicator.visibility = View.VISIBLE
        }
        query_refresh_button.setOnClickListener {
            initData()
            query_refresh_button.visibility = View.INVISIBLE
            progress_indicator.visibility = View.VISIBLE
        }
    }

    private fun initSearchView() {
        disposables.add(Observable.create<String> {
            search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = false

                override fun onQueryTextChange(newText: String): Boolean {
                    it.onNext(newText)
                    return false
                }
            })
        }
            .filter { it.isNotEmpty() }
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe {
                viewModel.searchMovies(it)
            }
        )
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
            viewModel.loadMovies()
        }
    }

    private fun hideLoadingBars() {
        query_refresh_button.visibility = View.VISIBLE
        progress_indicator.visibility = View.INVISIBLE
        progress_bar.visibility = View.INVISIBLE
        swipe_refresh.isRefreshing = false
    }

    private fun showSnackBar(string: String) {
        Snackbar.make(main_view, string, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}