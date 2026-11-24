package com.movielog.mobileapps2026_2401321071

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.movielog.mobileapps2026_2401321071.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun activityLaunchesSuccessfully() {
        activityRule.scenario.onActivity { activity ->
            assert(activity != null)
        }
    }
}
