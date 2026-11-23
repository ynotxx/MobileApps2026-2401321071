package com.movielog.mobileapps2026_2401321071.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_history",
    foreignKeys = [
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["localId"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["movieId"])]
)
data class MovieHistory(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    val movieId: Long,
    val watchedAt: Long,
    val rating: Float? = null
)
