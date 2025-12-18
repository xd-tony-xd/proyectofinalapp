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

@RunWith(AndroidJUnit4::class)
class LoginStressUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun multiple_login_attempts_stress_test() {

        repeat(10) {

            // Email inválido
            onView(withId(R.id.etEmail))
                .perform(clearText(), typeText("correo@mal"), closeSoftKeyboard())

            // Password inválido
            onView(withId(R.id.etPassword))
                .perform(clearText(), typeText("12"), closeSoftKeyboard())

            // Click Login
            onView(withId(R.id.btnLogin))
                .perform(click())

            // ✅ VALIDACIÓN DE ESTRÉS CORRECTA
            // La app sigue viva y los componentes siguen visibles
            onView(withId(R.id.etEmail))
                .check(matches(isDisplayed()))

            onView(withId(R.id.btnLogin))
                .check(matches(isClickable()))
        }
    }
}
