package com.movielog.mobileapps2026_2401321071.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.movielog.mobileapps2026_2401321071.R
import com.movielog.mobileapps2026_2401321071.data.AppDatabase
import com.movielog.mobileapps2026_2401321071.data.Movie
import com.movielog.mobileapps2026_2401321071.data.MovieRepository
import com.movielog.mobileapps2026_2401321071.data.RatingValidator
import com.movielog.mobileapps2026_2401321071.databinding.FragmentMovieDetailsBinding
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModel
import com.movielog.mobileapps2026_2401321071.viewmodel.MovieViewModelFactory
import kotlinx.coroutines.launch

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext().applicationContext)
        val repository = MovieRepository(db.movieDao(), db.movieHistoryDao())
        MovieViewModelFactory(repository)
    }

    private var currentMovie: Movie? = null
    private var isInitialDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localId = arguments?.getInt("localId")?.toLong() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMovieById(localId).collect { movie ->
                    movie?.let {
                        currentMovie = it
                        binding.tvTitle.text = it.title
                        binding.tvSynopsis.text = it.synopsis
                        binding.switchWatchlist.isChecked = it.inWatchlist
                        binding.switchWatched.isChecked = it.watched

                        if (!isInitialDataLoaded) {
                            binding.etRating.setText(it.rating?.toString() ?: "")
                            binding.etNotes.setText(it.notes ?: "")
                            isInitialDataLoaded = true
                        }
                    }
                }
            }
        }

        binding.switchWatchlist.setOnCheckedChangeListener { _, isChecked ->
            currentMovie?.let { movie ->
                if (movie.inWatchlist != isChecked) {
                    viewModel.updateMovie(movie.copy(inWatchlist = isChecked))
                }
            }
        }

        binding.switchWatched.setOnCheckedChangeListener { _, isChecked ->
            currentMovie?.let { movie ->
                if (movie.watched != isChecked) {
                    if (isChecked) {
                        val ratingStr = binding.etRating.text?.toString()?.trim()
                        val ratingDouble = if (ratingStr.isNullOrEmpty()) null else ratingStr.toDoubleOrNull()

                        if (!ratingStr.isNullOrEmpty() && ratingDouble == null) {
                            binding.tilRating.error = getString(R.string.invalid_rating_error)
                            binding.switchWatched.isChecked = false
                            return@setOnCheckedChangeListener
                        }

                        if (!RatingValidator.isValid(ratingDouble)) {
                            binding.tilRating.error = getString(R.string.invalid_rating_error)
                            binding.switchWatched.isChecked = false
                            return@setOnCheckedChangeListener
                        }

                        binding.tilRating.error = null
                        val notesText = binding.etNotes.text?.toString()?.trim()
                        viewModel.markAsWatched(movie, ratingDouble, notesText)
                    } else {
                        viewModel.unmarkAsWatched(movie)
                        binding.etRating.setText("")
                        binding.tilRating.error = null
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val movie = currentMovie ?: return@setOnClickListener
            val ratingStr = binding.etRating.text?.toString()?.trim()
            val ratingDouble = if (ratingStr.isNullOrEmpty()) null else ratingStr.toDoubleOrNull()

            if (!ratingStr.isNullOrEmpty() && ratingDouble == null) {
                binding.tilRating.error = getString(R.string.invalid_rating_error)
                return@setOnClickListener
            }

            if (!RatingValidator.isValid(ratingDouble)) {
                binding.tilRating.error = getString(R.string.invalid_rating_error)
                return@setOnClickListener
            }

            binding.tilRating.error = null

            val notesStr = binding.etNotes.text?.toString()?.trim()
            if (binding.switchWatched.isChecked) {
                viewModel.markAsWatched(movie, ratingDouble, notesStr)
            } else {
                viewModel.updateMovie(movie.copy(
                    rating = ratingDouble,
                    notes = notesStr
                ))
            }
        }

        binding.btnShare.setOnClickListener {
            val movie = currentMovie ?: return@setOnClickListener
            val shareText = buildString {
                append("Check out ")
                append(movie.title)
                append(" (")
                append(movie.year)
                append(")!\n")
                if (movie.rating != null) {
                    append("Rating: ")
                    append(movie.rating)
                    append(" / 10\n")
                }
                if (!movie.notes.isNullOrEmpty()) {
                    append("Notes: ")
                    append(movie.notes)
                }
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_movie)))
        }

        binding.btnDelete.setOnClickListener {
            currentMovie?.let { movie ->
                viewModel.deleteMovie(movie)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
