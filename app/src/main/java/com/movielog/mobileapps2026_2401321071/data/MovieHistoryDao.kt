package com.movielog.mobileapps2026_2401321071.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: MovieHistory): Long

    @Query("SELECT * FROM movie_history ORDER BY watchedAt DESC")
    fun getAllHistory(): Flow<List<MovieHistory>>
}
