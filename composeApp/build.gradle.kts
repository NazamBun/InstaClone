import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

// Lecture des secrets depuis local.properties (NON versionné).
// Permet d'éviter d'avoir des clés en dur dans le code source.
val secrets: Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val supabaseUrl: String = secrets.getProperty("SUPABASE_URL", "")
val supabaseAnonKey: String = secrets.getProperty("SUPABASE_ANON_KEY", "")

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val iosArm64Target = iosArm64()
    val iosSimulatorArm64Target = iosSimulatorArm64()
    listOf(iosArm64Target, iosSimulatorArm64Target).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
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

                implementation(libs.ktor.client.core)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.compose.viewmodel.navigation)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kamel.image)
                implementation(libs.kamel.image.default)

                implementation(libs.coil.compose)
                implementation(compose.materialIconsExtended)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("com.github.yalantis:ucrop:2.2.8")
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
                implementation(libs.ktor.client.android)
            }
        }

        val iosArm64Main by getting {
            dependencies { implementation(libs.ktor.client.darwin) }
        }
        val iosSimulatorArm64Main by getting {
            dependencies { implementation(libs.ktor.client.darwin) }
        }
    }
}

android {
    namespace = "com.nazam.instaclone"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.nazam.instaclone"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        // ✅ Lu depuis local.properties (jamais committé)
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
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
