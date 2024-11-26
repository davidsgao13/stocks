plugins {
    alias(libs.plugins.android.application) // Android application plugin
    alias(libs.plugins.kotlin.android) // Kotlin support in Android
    alias(libs.plugins.kotlin.parcelize) // Parcelize plugin for Parcelable
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp) // Kotlin Symbol Processing with version
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.secrets)
    alias(libs.plugins.hilt)
    alias(libs.plugins.androidx.room)
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

android {
    namespace = "com.example.stocks"
    compileSdk = 35

    defaultConfig {
        buildFeatures {
            buildConfig = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

        applicationId = "com.example.stocks"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

/**
 * Adding a schema export in Room allows the database structure to be captured in JSON files for
 * each version of the database. These schema files are essential for tracking changes to the
 * database over time and ensuring proper migrations between database versions. Room enforces schema
 * exporting by default because maintaining a migration history is critical for production apps
 * with persistent data. It helps with migrations and debugging.
 */

room {
    schemaDirectory("schemas") // Use a String path
}

dependencies {
    // General libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.junit)

    // OpenCSV
    implementation(libs.opencsv)

    //Compose dependencies
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.swiperefresh)

    // Compose Nav Destinations
    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.destinations.core)
    ksp(libs.ksp)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    // NEVER EVER EVER USE KSP AND KAPT WITH HILT HILT COMPILER
    // THIS TOOK ME TWO HOURS TO DEBUG
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
}