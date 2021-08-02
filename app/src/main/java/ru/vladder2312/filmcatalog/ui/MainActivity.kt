package ru.vladder2312.filmcatalog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.vladder2312.filmcatalog.R
import ru.vladder2312.filmcatalog.databinding.ActivityMainBinding
import ru.vladder2312.filmcatalog.domain.Movie
import java.util.concurrent.TimeUnit

/**
 * Главная активность приложения
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
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
        binding.queryErrorLayout.root.isVisible = false
        if (it.isEmpty()) {
            binding.notFoundLayout.root.isVisible = true
            binding.notFoundLayout.notFoundText.text = getString(R.string.not_found, binding.searchView.query)
        } else {
            binding.notFoundLayout.root.isVisible = false
        }
    }
    private val errorsObserver = Observer<String> {
        if (movieAdapter.itemCount == 0) {
            binding.queryErrorLayout.root.isVisible = true
            binding.queryErrorLayout.queryErrorText.text = getString(R.string.query_error)
        } else {
            binding.queryErrorLayout.root.isVisible = false
        }
        hideLoadingBars()
        showSnackBar(getString(R.string.connection_error))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue))
        binding.swipeRefresh.setOnRefreshListener {
            initData()
            binding.progressIndicator.isVisible = true
        }
        binding.queryErrorLayout.queryRefreshButton.setOnClickListener {
            initData()
            binding.queryErrorLayout.queryRefreshButton.isVisible = false
            binding.progressIndicator.isVisible = true
        }
    }

    private fun initSearchView() {
        disposables.add(Observable.create<String> {
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        binding.recyclerMovie.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerMovie.adapter = movieAdapter
    }

    private fun initData() {
        if (binding.searchView.query.isNotEmpty()) {
            viewModel.searchMovies(binding.searchView.query.toString())
        } else {
            viewModel.loadMovies()
        }
    }

    private fun hideLoadingBars() {
        binding.queryErrorLayout.queryRefreshButton.isVisible = true
        binding.progressIndicator.isVisible = false
        binding.progressBar.isVisible = false
        binding.swipeRefresh.isRefreshing = false
    }

    private fun showSnackBar(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}