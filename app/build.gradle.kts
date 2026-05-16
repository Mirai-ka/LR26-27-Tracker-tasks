import org.gradle.accessors.dm.LibrariesForLibsInPluginsBlock

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") version "4.4.2" apply false
}

android {
    namespace = "com.example.lr26_27_tracker_tasks"
    compileSdk = 35  // Меняем с 36 на 35 (более стабильный)

    defaultConfig {
        applicationId = "com.example.lr26_27_tracker_tasks"
        minSdk = 24
        targetSdk = 35  // Меняем на 35
        versionCode = 2  // Увеличиваем для публикации
        versionName = "1.1"  // Новая версия

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Настройка подписи для релиза
    signingConfigs {
        create("release") {
            // Используем переменные окружения или локальный файл
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore/tasktracker.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true  // Включаем сжатие
            isShrinkResources = true  // Удаляем неиспользуемые ресурсы
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ===== Core Android =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ===== Jetpack Compose =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // ===== Kotlin Coroutines =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ===== Firebase (Authentication) =====
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // ===== Supabase =====
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.3.0")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.3.0")
    implementation("io.github.jan-tennert.supabase:auth-kt:2.3.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.3.0")

    // ===== Retrofit для HTTP клиента =====
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ===== Gson =====
    implementation("com.google.code.gson:gson:2.10.1")

    // ===== Encrypted SharedPreferences (безопасное хранение) =====
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ===== Gson для сохранения задач =====
    implementation("com.google.code.gson:gson:2.10.1")

    // ===== Testing =====
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// Подключаем Google Services (требует google-services.json в папке app/)
apply(plugin = "com.google.gms.google-services")