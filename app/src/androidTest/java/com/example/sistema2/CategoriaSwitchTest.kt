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
class CategoriaSwitchTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun multiple_category_switch_does_not_crash() {

        // 🔐 Login
        onView(withId(R.id.etEmail))
            .perform(typeText("renzo@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.etPassword))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin))
            .perform(click())

        // ⏳ Esperar carga
        Thread.sleep(1500)

        // 📂 Entrar a categorías
        onView(withId(R.id.nav_categorias))
            .perform(click())

        Thread.sleep(1000)

        // 🔁 Estrés: cambios repetidos SIN validar vistas internas
        repeat(8) {
            onView(isRoot()).perform(swipeLeft())
            Thread.sleep(300)
            onView(isRoot()).perform(swipeRight())
            Thread.sleep(300)
        }

        // ✅ Test pasa si NO hay crash
    }
}
