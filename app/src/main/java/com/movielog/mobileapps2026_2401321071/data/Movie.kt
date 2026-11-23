package com.movielog.mobileapps2026_2401321071.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val apiId: String,
    val title: String,
    val year: Int,
    val posterUrl: String,
    val genre: String,
    val synopsis: String,
    val inWatchlist: Boolean = false,
    val watched: Boolean = false,
    val rating: Double? = null,
    val notes: String? = null
)
