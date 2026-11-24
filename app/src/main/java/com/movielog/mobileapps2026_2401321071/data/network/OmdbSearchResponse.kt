package com.movielog.mobileapps2026_2401321071.data.network

import com.google.gson.annotations.SerializedName

data class OmdbSearchResponse(
    @SerializedName("Search") val searchResults: List<OmdbSearchResult>?,
    @SerializedName("totalResults") val totalResults: String?,
    @SerializedName("Response") val response: String,
    @SerializedName("Error") val error: String?
)

data class OmdbSearchResult(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbId: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val posterUrl: String
)
