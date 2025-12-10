import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.wardrobedigital"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.wardrobedigital"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SENOPATI_BASE_URL", "\"https://senopati.its.ac.id/senopati-lokal-dev/\"")
        buildConfigField("String", "SENOPATI_BASE_URL_AKA", "\"https://senopati-api.vercel.app/\"")
        buildConfigField("String", "REMOVEBG_BASE_URL", "\"https://api.remove.bg/v1.0/\"")

        // Gunakan objek 'localProperties' yang sudah didefinisikan
        val removeBgApiKey = localProperties.getProperty("REMOVE_BG_API_KEY") ?: ""
        buildConfigField("String", "REMOVEBG_API_KEY", "\"$removeBgApiKey\"")
        buildConfigField("boolean", "ENABLE_LOGS", "true")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // Retrofit & OkHttp untuk komunikasi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val lifecycle_version = "2.8.3" // Versi terbaru saat ini
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Opsional, tapi sangat direkomendasikan untuk Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version") // Prosesor anotasi
    // Opsional - untuk dukungan RxJava atau Coroutines, tapi kita pakai LiveData
    implementation("androidx.room:room-common:$room_version")
    // Library Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0") // Pastikan versi terbaru
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    // Room components
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
}