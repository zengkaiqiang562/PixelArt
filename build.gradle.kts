// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.github.ben-manes.versions") version "0.45.0"
}

buildscript {
    dependencies {
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")

        // TODO 去掉 Firebase
//        classpath("com.google.gms:google-services:4.3.15")  // Google Services plugin
//        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.4")

        classpath("com.android.tools.build:gradle:7.4.2")
        classpath(kotlin("gradle-plugin", "1.8.10"))
    }
}

//plugins {
//    id("com.android.application") version "8.1.1" apply false
//    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
//}
