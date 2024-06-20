plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.levalens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.levalens"
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
}

dependencies {
    // Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    implementation(libs.fragment)
    implementation(libs.fragment.ktx)

    // OpenCV library
    implementation(project(":opencv"))

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit.v113)
    testImplementation(libs.androidx.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockito.core)

    // TensorFlow Lite dependencies
    implementation(libs.tensorflow.lite.task.vision.play.services)
    implementation(libs.play.services.tflite.gpu)
    implementation(libs.tensorflow.lite)

    // Android CameraX dependencies
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.extensions)

    // Image loading library
    implementation(libs.glide)
}
