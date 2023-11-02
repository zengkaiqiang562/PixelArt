plugins {
//    `groovy`
    `kotlin-dsl`
}

//sourceSets {
//    main {
//        groovy {
//            srcDir("src/main/groovy") // 存放 groovy 源码文件的路径
//        }
//
//        resources {
//            srcDir("src/main/resources") // 存放插件用到的资源文件的路径
//        }
//    }
//}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/commons-codec-1.11.jar"))
}

/**
 * fix bug:
 * Entry META-INF/gradle-plugins/com.deploy.plugin.properties is a duplicate but no duplicate handling strategy has been set.
 */
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}