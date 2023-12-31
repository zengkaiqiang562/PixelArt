pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.google.com") } // 解决 Gradle Sync 时报错： Could not find intellij-core.jar
        maven { setUrl("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea") }
    }
}

rootProject.name = "RelaxingPixelColoringBookGame"
include(":app")
include(":MPChartLib")
include(":banner")
include(":magicindicator")
include(":refresh-layout-kernel")
include(":refresh-drawable-paint")
include(":refresh-footer-classics")
include(":refresh-header-classics")
include(":GroupRecyclerViewAdapter")
