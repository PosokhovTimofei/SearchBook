plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.searcbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.searchbook"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Основные зависимости
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    // Compose libraries
    implementation (libs.androidx.ui.v150)
    implementation (libs.androidx.material3.v110)
    implementation (libs.androidx.navigation.compose.v250)

    // Lifecycle libraries for LiveData
    implementation (libs.androidx.lifecycle.runtime.ktx.v250)
    implementation (libs.androidx.lifecycle.viewmodel.compose.v250)

    // Coil for image loading
    implementation (libs.coil.compose.v220)

    // UI
    implementation(libs.material3)
    implementation(libs.coil.compose)

    // Retrofit + GSON + Scalars + Logging
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.logging.interceptor)

    // Корутины
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation (libs.androidx.runtime.livedata)
    implementation(libs.coil.compose)

    implementation (libs.androidx.navigation.compose.v277)
    implementation (libs.androidx.material)
    implementation (libs.androidx.lifecycle.viewmodel.compose.v270)

    implementation(libs.androidx.material3.v120)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.coil.compose)

    implementation (libs.translate)

    implementation (libs.kotlinx.coroutines.play.services)




    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
