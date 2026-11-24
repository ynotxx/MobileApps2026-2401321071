package com.movielog.mobileapps2026_2401321071

import com.movielog.mobileapps2026_2401321071.data.RatingValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RatingValidatorTest {

    @Test
    fun nullRating_isValid() {
        assertTrue(RatingValidator.isValid(null))
    }

    @Test
    fun validRatings_areValid() {
        assertTrue(RatingValidator.isValid(0.0))
        assertTrue(RatingValidator.isValid(5.5))
        assertTrue(RatingValidator.isValid(7.2))
        assertTrue(RatingValidator.isValid(10.0))
    }

    @Test
    fun outOfRangeRatings_areInvalid() {
        assertFalse(RatingValidator.isValid(-0.1))
        assertFalse(RatingValidator.isValid(10.1))
        assertFalse(RatingValidator.isValid(15.0))
    }

    @Test
    fun nonTenthsIncrements_areInvalid() {
        assertFalse(RatingValidator.isValid(7.25))
        assertFalse(RatingValidator.isValid(8.33))
        assertFalse(RatingValidator.isValid(9.99))
    }
}
