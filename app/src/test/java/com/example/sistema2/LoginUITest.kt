package com.example.sistema2

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.time.Duration
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options

import org.openqa.selenium.By



class LoginUITest {

    private lateinit var driver: AndroidDriver

    @Before
    fun setUp() {

        val options = UiAutomator2Options()
            .setDeviceName("Android Device")
            .setPlatformName("Android")
            .setAutomationName("UiAutomator2")
            .setAppPackage("com.example.sistema2")
            .setAppActivity(".ui.login.LoginActivity")
            .setAppWaitActivity("*")
            .setNoReset(true)

        driver = AndroidDriver(
            URL("http://127.0.0.1:4723"),
            options
        )

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
    }

    @Test
    fun loginTest() {

        driver.findElement(By.id("com.example.sistema2:id/etEmail"))
            .sendKeys("correo@mal")

        driver.findElement(By.id("com.example.sistema2:id/etPassword"))
            .sendKeys("123")

        driver.findElement(By.id("com.example.sistema2:id/btnLogin")).click()

        val errorText =
            driver.findElement(By.id("com.example.sistema2:id/tvError"))

        Assert.assertTrue(errorText.isDisplayed)
    }

    @After
    fun tearDown() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }
}