// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false // For Android application projects
    alias(libs.plugins.android.library) apply false    // For Android library modules
    alias(libs.plugins.kotlin.android) apply false     // For Kotlin support in Android
    alias(libs.plugins.compose.compiler) apply false   // For Compose compiler.compose) apply false
    alias(libs.plugins.hilt) apply false  // Hilt plugin for dependency injection
}

// Task to clean the build directory
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory) // Replaces rootProject.buildDir
}