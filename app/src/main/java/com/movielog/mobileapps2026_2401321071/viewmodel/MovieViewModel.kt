package com.movielog.mobileapps2026_2401321071.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movielog.mobileapps2026_2401321071.data.Movie
import com.movielog.mobileapps2026_2401321071.data.MovieHistory
import com.movielog.mobileapps2026_2401321071.data.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    val allMovies: StateFlow<List<Movie>> = repository.allMovies.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchlistMovies: StateFlow<List<Movie>> = repository.watchlistMovies.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchedMovies: StateFlow<List<Movie>> = repository.watchedMovies.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val viewingHistory: StateFlow<List<MovieHistory>> = repository.viewingHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getMovieById(localId: Long): Flow<Movie?> {
        return repository.getMovieById(localId)
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insertMovie(movie)
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
        }
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(inWatchlist = !movie.inWatchlist))
        }
    }

    fun markAsWatched(movie: Movie, rating: Double? = null, notes: String? = null) {
        viewModelScope.launch {
            repository.markAsWatched(movie, rating, notes)
        }
    }

    fun unmarkAsWatched(movie: Movie) {
        viewModelScope.launch {
            repository.unmarkAsWatched(movie)
        }
    }

    fun logMovieViewing(movieId: Long, rating: Float? = null) {
        viewModelScope.launch {
            val history = MovieHistory(
                movieId = movieId,
                watchedAt = System.currentTimeMillis(),
                rating = rating
            )
            repository.insertHistory(history)
        }
    }
}

class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
