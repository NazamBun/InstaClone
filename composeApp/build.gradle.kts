import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.kotlinSerialization)


}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Cibles iOS
    val iosArm64Target = iosArm64()
    val iosSimulatorArm64Target = iosSimulatorArm64()

    listOf(
        iosArm64Target,
        iosSimulatorArm64Target
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ✅ MOTEUR RÉSEAU POUR COIL (ANDROID)
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
        }

        // ⭐ commun (Android + iOS)
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Supabase v3.x (KMP)
                implementation("io.github.jan-tennert.supabase:auth-kt:${libs.versions.supabase.get()}")
                implementation("io.github.jan-tennert.supabase:postgrest-kt:${libs.versions.supabase.get()}")
                implementation("io.github.jan-tennert.supabase:storage-kt:${libs.versions.supabase.get()}")

                // Ktor core (partagé)
                implementation(libs.ktor.client.core)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)

                // Kamel
                implementation(libs.kamel.image)
                implementation(libs.kamel.image.default)

                // Coil
                implementation(libs.coil.compose)

                implementation(compose.materialIconsExtended)


            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        // ⭐ Android
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // moteur HTTP pour Android
                implementation(libs.ktor.client.android)
            }
        }

        // ⭐ iOS Arm64
        val iosArm64Main by getting {
            dependencies {
                // moteur HTTP pour iOS (Darwin)
                implementation(libs.ktor.client.darwin)
            }
        }

        // ⭐ iOS Simulator (Arm64)
        val iosSimulatorArm64Main by getting {
            dependencies {
                // même moteur HTTP
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "com.nazam.instaclone"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nazam.instaclone"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}