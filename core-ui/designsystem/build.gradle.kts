plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
val composeCompilerExtensionVersion: String by rootProject.extra
val composeMd3Version: String by rootProject.extra
val accompanistVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra
val ktxCoreVersion: String by rootProject.extra
val materialVersion: String by rootProject.extra
val appCompatVersion: String by rootProject.extra

@Suppress("UnstableApiUsage")
android {
    namespace = "io.github.junkfood.common"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerExtensionVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core-ui:color"))
    implementation("androidx.core:core-ktx:$ktxCoreVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    implementation("androidx.compose.material3:material3:$composeMd3Version")
    implementation("com.google.android.material:material:$materialVersion")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}