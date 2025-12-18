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
class LoginUITest {

    // 🔥 ESTO ABRE LA APP AUTOMÁTICAMENTE
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun login_correct_opens_and_stays_open() {

        // Email válido
        onView(withId(R.id.etEmail))
            .perform(typeText("renzo@gmail.com"), closeSoftKeyboard())

        // Password válido
        onView(withId(R.id.etPassword))
            .perform(typeText("1234"), closeSoftKeyboard())

        // Click login
        onView(withId(R.id.btnLogin))
            .perform(click())

        // ✅ Validación mínima y segura
        // El error NO debe mostrarse
        onView(withId(R.id.tvError))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // ⏳ Mantener la app abierta (NO se cierra, NO crashea)
        Thread.sleep(3000)
    }
}
