package com.movielog.mobileapps2026_2401321071.data

import com.movielog.mobileapps2026_2401321071.data.network.OmdbSearchResult
import com.movielog.mobileapps2026_2401321071.data.network.RetrofitClient
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

    suspend fun searchOnlineMovies(query: String): List<OmdbSearchResult> {
        val response = RetrofitClient.apiService.searchMovies(query)
        if (response.response.equals("True", ignoreCase = true) && response.searchResults != null) {
            return response.searchResults
        } else {
            throw Exception(response.error ?: "No movies found.")
        }
    }

    suspend fun importMovieFromApi(imdbId: String, inWatchlist: Boolean = false, watched: Boolean = false): Movie {
        val existing = movieDao.getMovieByApiId(imdbId)
        if (existing != null) {
            val updated = existing.copy(
                inWatchlist = if (inWatchlist) true else existing.inWatchlist,
                watched = if (watched) true else existing.watched
            )
            movieDao.updateMovie(updated)
            return updated
        }
        val detail = RetrofitClient.apiService.getMovieDetails(imdbId)
        val entity = detail.toMovieEntity(inWatchlist = inWatchlist, watched = watched)
        val id = movieDao.insertMovie(entity)
        return entity.copy(localId = id)
    }

    suspend fun insertMovie(movie: Movie): Long {
        val existing = movieDao.getMovieByApiId(movie.apiId)
        if (existing != null) return existing.localId
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
