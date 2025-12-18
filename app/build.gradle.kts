plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.sistema2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sistema2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    // 🔥 BLOQUE CLAVE PARA SELENIUM + APPIUM
    packaging {
        resources {
            excludes += listOf(
                "META-INF/versions/**",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }

}

dependencies {

    // 🔹 Básico
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // 🔹 Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // 🔹 Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 🔹 ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // 🔹 Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    // 🔹 CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // 🔹 Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")

    // 🔹 Android Instrumentation Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")

    // ===== APPIUM / SELENIUM (JVM TESTS) =====
    testImplementation("io.appium:java-client:9.2.2")
    androidTestImplementation("org.seleniumhq.selenium:selenium-java:4.12.1")
    testImplementation(kotlin("test"))


    androidTestImplementation("io.appium:java-client:9.2.2")
    androidTestImplementation("org.seleniumhq.selenium:selenium-java:4.21.0")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

}
