// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false // For Android app modules
    alias(libs.plugins.kotlin.android) apply false      // For Kotlin support in Android
    alias(libs.plugins.kotlin.parcelize) apply false    // For Parcelable support
    alias(libs.plugins.kotlin.serialization) apply false // For Kotlin serialization
    alias(libs.plugins.ksp) apply false                 // Kotlin Symbol Processing
    alias(libs.plugins.compose.compiler) apply false    // For Jetpack Compose Compiler
    alias(libs.plugins.google.secrets) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.androidx.room) apply false
}

// Task to clean the build directory
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory) // Replaces rootProject.buildDir
}