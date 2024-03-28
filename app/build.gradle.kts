plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.weatherwise"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherwise"
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

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation("androidx.preference:preference:1.2.1")
    val nav_version = "2.7.7"
    val room_version = "2.6.1"
    val preference_version = "1.2.1"
    val coroutine_version = "1.7.1"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    //Google Services
    implementation("com.google.android.gms:play-services-location:21.2.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //coroutines
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    //Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //Preferences Fragment
    implementation("androidx.preference:preference-ktx:$preference_version")

    //Open Street Map
    implementation ("org.osmdroid:osmdroid-android:6.1.18")


    // AndroidX Test - JVM testing
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("androidx.test.ext:junit:1.1.5")


    // Dependencies for local unit tests
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.robolectric:robolectric:4.8")


    // hamcrest
    testImplementation ("org.hamcrest:hamcrest:2.2")
    testImplementation ("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")


    // InstantTaskExecutorRule
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")

    //kotlinx-coroutines
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutine_version")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutine_version")


    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("com.jakewharton.timber:timber:5.0.1")



}