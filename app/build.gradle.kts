plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // TODO 去掉 Firebase
//    id("com.google.gms.google-services")  // Google Services plugin
//    id("com.google.firebase.crashlytics") // Apply the Crashlytics Gradle plugin

    id("com.deploy.plugin") // Custom Gradle Plugin in buildSrc
}

android {
    namespace = deployExt.debugNamespace
    compileSdk = 33

    defaultConfig {
        applicationId = deployExt.curPkgName
        minSdk = 23
        targetSdk = 33
        versionCode = deployExt.versionCode
        versionName = deployExt.versionName

        // TODO
//        externalNativeBuild {
//            ndk {
//                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
//            }
//        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // vectorDrawables {
        //     useSupportLibrary = true
        // }

        buildConfig(this)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), deployExt.proguardPath)

            // TODO 去掉 Firebase
//             // 设置是否要自动上传（默认为true，要自动上传），测试环境为 false，正式环境为 true
//            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
//                mappingFileUploadEnabled = deployExt.uploadMappingFile
//            }

            // 放开注释，aab 包体积会减小。
            // 因为会把 aab 包中 BUNDLE_MEATADATA 目录下的 debugsymbols 文件夹去掉，即不添加调试符号
            // 不添加调试符号虽然可以减小 aab 的体积，但 native crash 时无法跟踪到问题代码
            ndk {
                debugSymbolLevel = "none"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // lint.apply {
    //     warning += "ExtraTranslation"
    //     warning += "ImpliedQuantity"
    //     informational += "MissingQuantity"
    //     informational += "MissingTranslation"

    //     disable += "BadConfigurationProvider"
    //     warning += "RestrictedApi"
    //     disable += "UseAppTint"

    //     disable += "RemoveWorkManagerInitializer"
    // }

    // compileOptions.isCoreLibraryDesugaringEnabled = true

    // ndkVersion = "25.1.8937393"

    buildFeatures {
//        compose = true
        buildConfig = true
    }

    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.4.3"
    // }

    // replace packagingOptions
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    bundle {
        /** @see https://developer.android.com/guide/app-bundle/configure-base?hl=zh-cn#disable_config_apks */
        abi {
            enableSplit = false
        }

        language {
            enableSplit = false
        }
    }

    viewBinding {
        enable = true
    }

    sourceSets {
        getByName("main") {
            assets.setSrcDirs(listOf(deployExt.assetsPath))
            java.setSrcDirs(listOf(deployExt.javaPath))
            res.setSrcDirs(listOf(deployExt.resPath, "src/main/debug/res-base", "src/main/debug/res-demo"))
            manifest.srcFile(deployExt.manifestPath)
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
//    implementation("androidx.activity:activity-compose:1.7.0")
//    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")

    add("implementation", project(":MPChartLib"))
    add("implementation", project(":banner"))
    add("implementation", project(":magicindicator"))
    add("implementation", project(":refresh-layout-kernel"))
    add("implementation", project(":refresh-footer-classics"))
    add("implementation", project(":refresh-header-classics"))
    add("implementation", project(":GroupRecyclerViewAdapter"))

    // add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.0.2")

    // implementation("androidx.work:work-multiprocess:2.8.1")
    // implementation("androidx.work:work-runtime-ktx:2.8.1")


    implementation(files("libs/commons-codec-1.11.jar"))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    implementation("com.airbnb.android:lottie:6.0.0")

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    api("com.github.getActivity:XXPermissions:18.5")

    implementation("androidx.room:room-runtime:2.5.1")
    annotationProcessor("androidx.room:room-compiler:2.5.1")

    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.blankj:utilcodex:1.31.0")
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")

    // TODO 去掉 Firebase
//    /* Firebase */
//    // Import the BoM for the Firebase platform
//    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
//    // Declare the dependencies for the Crashlytics and Analytics libraries
//    // When using the BoM, you don't specify versions in Firebase library dependencies
//    implementation("com.google.firebase:firebase-crashlytics")
//    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.google.firebase:firebase-config")
//    api("com.google.firebase:firebase-messaging:23.2.1")

    // TODO 去掉 Facebook
//    /* Facebook */
//    implementation("com.facebook.android:facebook-core:12.1.0")
//    implementation("com.facebook.android:facebook-applinks:12.1.0")

//    /* Adjust */
//    implementation("com.adjust.sdk:adjust-android:4.33.5")
//    implementation("com.android.installreferrer:installreferrer:2.2")
//    // Add the following if you are using the Adjust SDK inside web views on your app
//    implementation("com.adjust.sdk:adjust-android-webbridge:4.33.5")
//    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
}

fun buildConfig(config: com.android.build.api.dsl.DefaultConfig) {
    config.buildConfigField("boolean", "ENABLE_LOG", "${deployExt.enableLog}")
    config.buildConfigField("boolean", "ENABLE_CRASH", "${deployExt.enableCrash}")

    config.buildConfigField("String", "URL_PRIVACY", "\"${deployExt.urlPrivacy}\"")
    config.buildConfigField("String", "URL_TERMS", "\"${deployExt.urlTerms}\"")
    config.buildConfigField("String", "FEEDBACK_EMAIL", "\"${deployExt.emailFeedback}\"")
    config.buildConfigField("String", "URL_OFFICIAL", "\"${deployExt.urlOfficial}\"")

//    config.buildConfigField("String", "BASE_URL", "\"${deployExt.baseUrl}\"")
//    config.buildConfigField("String", "PATH_CONFIG", "\"${deployExt.pathConfig}\"")

    config.buildConfigField("String", "PATH_LOCATION_1", "\"${deployExt.pathLocation1}\"")
    config.buildConfigField("String", "PATH_LOCATION_2", "\"${deployExt.pathLocation2}\"")
    config.buildConfigField("String", "PATH_LOCATION_3", "\"${deployExt.pathLocation3}\"")
    config.buildConfigField("String", "PATH_LOCATION_4", "\"${deployExt.pathLocation4}\"")

//        manifestPlaceholders["fbId"] = "222222222222222"
//        manifestPlaceholders["fbToken"] = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

    // TODO 去掉 Facebook
//    config.resValue("string", "facebook_id", deployExt.facebookId)
//    config.resValue("string", "facebook_token", deployExt.facebookToken)

    config.resValue("string", "adjust_token", deployExt.adjustToken)
//    config.resValue("string", "admob_id", deployExt.admobId)
//    config.resValue("string", "applovin_key", deployExt.applovinKey)

    // TODO 去掉 Facebook
//    config.buildConfigField("String", "FACEBOOK_ID", "\"${deployExt.facebookId}\"")
//    config.buildConfigField("String", "FACEBOOK_TOKEN", "\"${deployExt.facebookToken}\"")
    config.buildConfigField("String", "ADJUST_TOKEN", "\"${deployExt.adjustToken}\"")

//    config.buildConfigField("String", "LOCAL_CONFIG", "\"${deployExt.localConfig}\"") // TODO 去掉本地全局配置
//    config.buildConfigField("String", "VPN_LIST", "\"${deployExt.vpnlist}\"")
}

tasks.register("removeProguardMappingFromReleaseIntermediateBundle", Zip::class.java) {
    description = "Remove proguard mapping from bundle file for Google Play upload"

    val name = "intermediary-bundle.tmp.aab"
    archiveFileName.convention(name);
    archiveFileName.set(name);

    destinationDirectory.set(file("${project.buildDir}/intermediates/intermediary_bundle/release/"))

    println(destinationDirectory.asFile.get().listFiles())

    from(zipTree("${project.buildDir}/intermediates/intermediary_bundle/release/intermediary-bundle.aab")) {
        exclude("BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map")
    }

    doLast {
        val source = "${project.buildDir}/intermediates/intermediary_bundle/release/intermediary-bundle.tmp.aab"
        val target = source.replace(".tmp.aab", ".aab")
        file(target).delete()
        file(source).renameTo(file(target))
    }
}

project.afterEvaluate {
    tasks.getByName("shrinkBundleReleaseResources").finalizedBy("removeProguardMappingFromReleaseIntermediateBundle")
}
