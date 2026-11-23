package com.movielog.mobileapps2026_2401321071.data

object RatingValidator {
    fun isValid(rating: Double?): Boolean {
        if (rating == null) return true
        if (rating < 0.0 || rating > 10.0) return false
        val tenths = Math.round(rating * 10)
        return Math.abs(rating * 10 - tenths) < 1e-5
    }
}
