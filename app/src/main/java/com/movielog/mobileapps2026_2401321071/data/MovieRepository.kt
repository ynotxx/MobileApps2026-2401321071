package com.movielog.mobileapps2026_2401321071.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val movieDao: MovieDao,
    private val movieHistoryDao: MovieHistoryDao
) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()
    val watchlistMovies: Flow<List<Movie>> = movieDao.getWatchlistMovies()
    val watchedMovies: Flow<List<Movie>> = movieDao.getWatchedMovies()
    val viewingHistory: Flow<List<MovieHistory>> = movieHistoryDao.getAllHistory()

    fun getMovieById(localId: Long): Flow<Movie?> {
        return movieDao.getMovieById(localId)
    }

    suspend fun insertMovie(movie: Movie): Long {
        return movieDao.insertMovie(movie)
    }

    suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun insertHistory(history: MovieHistory): Long {
        return movieHistoryDao.insertHistory(history)
    }

    suspend fun markAsWatched(movie: Movie, rating: Double? = null, notes: String? = null) {
        val updatedMovie = movie.copy(
            watched = true,
            rating = rating,
            notes = notes ?: movie.notes
        )
        movieDao.updateMovie(updatedMovie)
        val history = MovieHistory(
            movieId = movie.localId,
            watchedAt = System.currentTimeMillis(),
            rating = rating?.toFloat()
        )
        movieHistoryDao.insertHistory(history)
    }

    suspend fun unmarkAsWatched(movie: Movie) {
        val updatedMovie = movie.copy(
            watched = false,
            rating = null
        )
        movieDao.updateMovie(updatedMovie)
    }
}
