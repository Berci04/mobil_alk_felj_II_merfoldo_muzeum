//buildscript {
//    dependencies {
//        classpath(libs.google.services)
//    }
//}
//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.androidApplication) apply false
//    id("com.google.gms.google-services") version "4.4.2" apply false
//}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin 8.2.2 (támogatja Gradle 8.4-et)
    id("com.android.application") version "8.3.0" apply false
    id("com.android.library")     version "8.3.0" apply false

    // Kotlin plugin 1.9.10 — illeszkedik az AGP 8.2.2-höz
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false

    // Google Services (Firebase) plugin maradhat
    id("com.google.gms.google-services") version "4.4.2" apply false
}