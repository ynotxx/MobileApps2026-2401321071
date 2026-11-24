package com.movielog.mobileapps2026_2401321071.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.movielog.mobileapps2026_2401321071.R
import com.movielog.mobileapps2026_2401321071.data.AppDatabase
import com.movielog.mobileapps2026_2401321071.data.MovieRepository
import com.movielog.mobileapps2026_2401321071.databinding.FragmentSearchBinding
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModel
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModelFactory
import com.movielog.mobileapps2026_2401321071.viewmodel.SearchUiState
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val repository = MovieRepository(db.movieDao(), db.movieHistoryDao())
        MovieViewModelFactory(repository)
    }

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(
            onAddWatchlistClick = { result ->
                viewModel.importMovieToWatchlist(result.imdbId)
            },
            onAddCollectionClick = { result ->
                viewModel.importMovieToCollection(result.imdbId)
            }
        )

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.etSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                viewModel.searchMovies(query)
                true
            } else {
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { state ->
                    when (state) {
                        is SearchUiState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvStateText.visibility = View.VISIBLE
                            binding.tvStateText.text = getString(R.string.search_prompt)
                        }
                        is SearchUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvStateText.visibility = View.GONE
                        }
                        is SearchUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.VISIBLE
                            binding.tvStateText.visibility = View.GONE
                            searchAdapter.submitList(state.results)
                        }
                        is SearchUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvStateText.visibility = View.VISIBLE
                            binding.tvStateText.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
