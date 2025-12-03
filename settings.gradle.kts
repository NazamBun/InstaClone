rootProject.name = "InstaClone"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        // Pour Android (filtré sur les libs Google)
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }

        // ⭐ Pour TOUTES les libs multiplateformes (dont Supabase)
        mavenCentral()

        // ⭐ Optionnel mais utile pour certaines libs KMP
        maven("https://jitpack.io")
    }
}

include(":composeApp")