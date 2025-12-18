package com.example.sistema2

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import java.net.URL

class LoginAppiumTest {

    // 👉 NULLABLE para evitar crash
    private var driver: AndroidDriver? = null

    @Before
    fun setUp() {
        val options = UiAutomator2Options()
            .setDeviceName("Pixel_8a")
            .setPlatformName("Android")
            .setAutomationName("UiAutomator2")
            .setAppPackage("com.example.sistema2")
            .setAppActivity("com.example.sistema2.ui.login.LoginActivity")
            .setNoReset(true)

        driver = AndroidDriver(
            URL("http://127.0.0.1:4723"),
            options
        )
    }

    @Test
    fun testLogin() {
        // Espera simple
        Thread.sleep(3000)

        driver?.findElement(By.id("etEmail"))
            ?.sendKeys("renzo@gmail.com")

        driver?.findElement(By.id("etPassword"))
            ?.sendKeys("1234")

        driver?.findElement(By.id("btnLogin"))
            ?.click()

        Thread.sleep(3000)
    }

    @After
    fun tearDown() {
        // 👉 SOLO cerrar si fue creado
        driver?.quit()
    }
}