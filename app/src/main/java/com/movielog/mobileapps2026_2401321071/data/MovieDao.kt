package com.movielog.mobileapps2026_2401321071.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE inWatchlist = 1 ORDER BY title ASC")
    fun getWatchlistMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE watched = 1 ORDER BY title ASC")
    fun getWatchedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE localId = :localId")
    fun getMovieById(localId: Long): Flow<Movie?>

    @Query("SELECT COUNT(*) FROM movies WHERE apiId = :apiId")
    suspend fun countByApiId(apiId: String): Int

    @Query("SELECT * FROM movies WHERE apiId = :apiId LIMIT 1")
    suspend fun getMovieByApiId(apiId: String): Movie?
}
