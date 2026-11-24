package com.movielog.mobileapps2026_2401321071

import com.movielog.mobileapps2026_2401321071.data.Movie
import com.movielog.mobileapps2026_2401321071.data.MovieHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieLogicTest {

    @Test
    fun movieMarkedWatched_updatesFieldsAndPreservesHistory() {
        val originalMovie = Movie(
            localId = 1,
            apiId = "tt12345",
            title = "Inception",
            year = 2010,
            posterUrl = "",
            genre = "Sci-Fi",
            synopsis = "A dream within a dream.",
            inWatchlist = true,
            watched = false,
            rating = null
        )

        val watchedMovie = originalMovie.copy(
            watched = true,
            rating = 8.5,
            notes = "Great movie"
        )

        val historyEntry = MovieHistory(
            historyId = 1,
            movieId = watchedMovie.localId,
            watchedAt = 1000000L,
            rating = 8.5f
        )

        assertTrue(watchedMovie.watched)
        assertEquals(8.5, watchedMovie.rating)
        assertEquals("Great movie", watchedMovie.notes)
        assertEquals(1L, historyEntry.movieId)

        val unwatchedMovie = watchedMovie.copy(
            watched = false,
            rating = null
        )

        assertFalse(unwatchedMovie.watched)
        assertNull(unwatchedMovie.rating)
        assertEquals("Great movie", unwatchedMovie.notes)
        assertEquals(1L, historyEntry.movieId)
    }

    @Test
    fun filteringMoviesByWatchlistAndWatched() {
        val movies = listOf(
            Movie(localId = 1, apiId = "m1", title = "A", year = 2020, posterUrl = "", genre = "", synopsis = "", inWatchlist = true, watched = false),
            Movie(localId = 2, apiId = "m2", title = "B", year = 2021, posterUrl = "", genre = "", synopsis = "", inWatchlist = false, watched = true),
            Movie(localId = 3, apiId = "m3", title = "C", year = 2022, posterUrl = "", genre = "", synopsis = "", inWatchlist = true, watched = true)
        )

        val watchlist = movies.filter { it.inWatchlist }
        val watched = movies.filter { it.watched }

        assertEquals(2, watchlist.size)
        assertEquals(2, watched.size)
    }
}
