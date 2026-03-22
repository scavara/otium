import java.util.Properties // ADD THIS IMPORT AT THE TOP

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.scavara.otium"
    compileSdk = 36 // Adjusted to a stable SDK version; change back to 36 if required for your setup

    defaultConfig {
        applicationId = "com.scavara.otium"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Logic to safely read the Unsplash key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val unsplashKey = localProperties.getProperty("UNSPLASH_ACCESS_KEY") ?: ""
        val quoteUrl = localProperties.getProperty("QUOTE_BASE_URL") ?: "https://fallback.url/"

        // 2. Define the fields
        buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"$unsplashKey\"")
        buildConfigField("String", "QUOTE_BASE_URL", "\"$quoteUrl\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true // CRITICAL: This was missing in your shared file
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

    buildFeatures {
        compose = true
        buildConfig = true // REQUIRED to use BuildConfig.UNSPLASH_ACCESS_KEY
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Retrofit & Gson for API calls
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Add to your dependencies block
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
}