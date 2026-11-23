package com.movielog.mobileapps2026_2401321071.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.movielog.mobileapps2026_2401321071.R
import com.movielog.mobileapps2026_2401321071.data.AppDatabase
import com.movielog.mobileapps2026_2401321071.data.Movie
import com.movielog.mobileapps2026_2401321071.data.MovieRepository
import com.movielog.mobileapps2026_2401321071.databinding.FragmentCollectionBinding
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModel
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModelFactory
import kotlinx.coroutines.launch

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val repository = MovieRepository(db.movieDao(), db.movieHistoryDao())
        MovieViewModelFactory(repository)
    }

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieAdapter = MovieAdapter { movie ->
            val bundle = bundleOf("localId" to movie.localId.toInt())
            findNavController().navigate(R.id.action_collectionFragment_to_movieDetailsFragment, bundle)
        }

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allMovies.collect { movies ->
                    movieAdapter.submitList(movies)
                }
            }
        }

        binding.fabAddMovie.setOnClickListener {
            val count = (movieAdapter.currentList.size + 1)
            val dummyMovie = Movie(
                apiId = "dummy_$count",
                title = "Dummy Movie $count",
                year = 2026,
                posterUrl = "",
                genre = "Action",
                synopsis = "This is a dummy movie entry for testing.",
                inWatchlist = false,
                watched = false
            )
            viewModel.addMovie(dummyMovie)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
