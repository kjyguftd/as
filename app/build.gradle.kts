import java.io.FileInputStream
import java.util.Properties


plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
}


val versionMajor = 0
val versionMinor = 0
val versionPatch = 1
val versionBuild = 0


val composeCompilerExtensionVersion: String by rootProject.extra
val ktxCoreVersion: String by rootProject.extra
val materialVersion: String by rootProject.extra
val appCompatVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra
val activityComposeVersion: String by rootProject.extra
val lifecycleVersion: String by rootProject.extra
val navigationVersion: String by rootProject.extra
val roomVersion: String by rootProject.extra
val accompanistVersion: String by rootProject.extra
val kotlinVersion: String by rootProject.extra
val composeMd3Version: String by rootProject.extra
val coilVersion: String by rootProject.extra
val exoPlayerVersion: String by rootProject.extra
val retrofitVersion: String by rootProject.extra
val rssParserVersion: String by rootProject.extra
val media3Version: String by rootProject.extra
val opmlParserVersion: String by rootProject.extra


val keystorePropertiesFile = rootProject.file("keystore.properties")


@Suppress("UnstableApiUsage")
android {
    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            getByName("debug")
            {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }
    compileSdk = 33
    defaultConfig {
        applicationId = "io.github.junkfood.heal"
        minSdk = 26
        targetSdk = 33
        versionCode = versionMajor * 1000 + versionMinor * 100 + versionPatch * 10 + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = false
    }
    namespace = "io.github.junkfood.heal"
}


dependencies {
    implementation(project(":core-ui:color"))
    implementation(project(":core-ui:designsystem"))
    implementation("androidx.core:core-ktx:$ktxCoreVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material3:material3:$composeMd3Version")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.compose.animation:animation-graphics:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")


    implementation("io.coil-kt:coil-compose:$coilVersion")


    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("com.tencent:mmkv:1.2.14")

    implementation("com.icosillion.podengine:podengine:2.4.1")

    // Media 3
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")

    // OPML parser https://github.com/mdewilde/opml-parser
    implementation("be.ceau:opml-parser:$opmlParserVersion")
    configurations {
        all {
            exclude("xpp3", "xpp3")
        }
    }

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

}