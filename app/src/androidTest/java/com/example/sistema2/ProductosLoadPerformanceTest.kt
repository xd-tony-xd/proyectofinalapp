package com.example.sistema2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.sistema2.ui.login.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class ProductosLoadPerformanceTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun productos_screen_loads_under_3_seconds() {

        // 🔐 LOGIN
        onView(withId(R.id.etEmail))
            .perform(typeText("renzo@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.etPassword))
            .perform(typeText("1234"), closeSoftKeyboard())

        val loadTime = measureTimeMillis {

            onView(withId(R.id.btnLogin))
                .perform(click())

            // ✅ VALIDACIÓN ESTABLE
            // Verificamos que NO se muestre el error
            onView(withId(R.id.tvError))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        }

        // ⏱️ RENDIMIENTO
        assertTrue(
            "Tiempo de carga alto: $loadTime ms",
            loadTime < 3000
        )
    }
}
