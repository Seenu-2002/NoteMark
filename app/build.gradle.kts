import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("io.kotzilla.kotzilla-plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.seenu.dev.android.notemark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.seenu.dev.android.notemark"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "X_USER_EMAIL", project.properties["xUserEmailHeader"].toString())
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Splash screen API
    implementation(libs.core.splashscreen)
    // Ktor
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.serialization.jvm)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.kotlinx.serialization)
    implementation(libs.ktor.client.content.negotiation)
    // Koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    // Kotzilla
    implementation(libs.kotzilla.sdk)
    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)
    // Timber
    implementation(libs.timber)
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // Constraint layout
    implementation(libs.androidx.constraintlayout.compose)
    // Adaptive layout
    implementation(libs.androidx.material3.window.size.class1)
    // Encrypted shared preferences
    implementation(libs.androidx.security.crypto)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

}