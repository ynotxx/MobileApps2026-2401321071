package com.movielog.mobileapps2026_2401321071.viewmodel

import com.movielog.mobileapps2026_2401321071.data.network.OmdbSearchResult

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<OmdbSearchResult>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
