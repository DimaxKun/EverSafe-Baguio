plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.eversafebaguio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eversafebaguio"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    viewBinding {
        enable = true
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
}

dependencies {
    implementation (libs.gson)
    implementation(libs.okhttp)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.constraintlayout)
    implementation (libs.recyclerview)
    implementation(libs.volley)
    implementation(libs.play.services.maps)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.navigation.ui.ktx)
}