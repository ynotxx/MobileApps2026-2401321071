package com.movielog.mobileapps2026_2401321071.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = API_KEY
    ): OmdbSearchResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = API_KEY
    ): OmdbMovieDetailResponse

    companion object {
        const val API_KEY = "b9bd48a6"
    }
}
