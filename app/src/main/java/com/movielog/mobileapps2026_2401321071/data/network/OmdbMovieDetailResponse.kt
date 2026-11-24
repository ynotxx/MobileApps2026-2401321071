package com.movielog.mobileapps2026_2401321071.data.network

import com.google.gson.annotations.SerializedName
import com.movielog.mobileapps2026_2401321071.data.Movie

data class OmdbMovieDetailResponse(
    @SerializedName("Title") val title: String?,
    @SerializedName("Year") val year: String?,
    @SerializedName("imdbID") val imdbId: String?,
    @SerializedName("Type") val type: String?,
    @SerializedName("Poster") val posterUrl: String?,
    @SerializedName("Genre") val genre: String?,
    @SerializedName("Plot") val synopsis: String?,
    @SerializedName("Response") val response: String?,
    @SerializedName("Error") val error: String?
) {
    fun toMovieEntity(inWatchlist: Boolean = false, watched: Boolean = false): Movie {
        val parsedYear = year?.take(4)?.toIntOrNull() ?: 0
        val validPoster = if (posterUrl != null && posterUrl != "N/A") posterUrl else ""
        return Movie(
            apiId = imdbId ?: "",
            title = title ?: "",
            year = parsedYear,
            posterUrl = validPoster,
            genre = genre ?: "",
            synopsis = synopsis ?: "",
            inWatchlist = inWatchlist,
            watched = watched
        )
    }
}
