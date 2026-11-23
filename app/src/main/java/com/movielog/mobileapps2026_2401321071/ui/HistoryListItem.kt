package com.movielog.mobileapps2026_2401321071.ui

import com.movielog.mobileapps2026_2401321071.data.MovieHistory

sealed class HistoryListItem {
    data class YearHeader(val year: Int) : HistoryListItem()
    data class ViewingEntry(val history: MovieHistory, val movieTitle: String) : HistoryListItem()
}
