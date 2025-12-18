package com.example.sistema2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.sistema2.ui.login.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductosScrollStressTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun massive_scroll_stress_test() {

        // 🔐 LOGIN
        onView(withId(R.id.etEmail))
            .perform(typeText("renzo@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.etPassword))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin))
            .perform(click())

        // ⏳ Pequeña espera para estabilizar UI
        Thread.sleep(1500)

        // 🔥 SCROLL MASIVO (ESTRÉS)
        repeat(20) {
            onView(isRoot()).perform(swipeUp())
            Thread.sleep(150)

            onView(isRoot()).perform(swipeDown())
            Thread.sleep(150)
        }

        // ✅ Si llega aquí: NO crash, NO freeze → test exitoso
    }
}
