buildscript {

    val composeVersion by extra("1.3.0-rc01")
    val lifecycleVersion by extra("2.6.0-alpha02")
    val navigationVersion by extra("2.5.2")
    val roomVersion by extra("2.4.3")
    val accompanistVersion by extra("0.25.1")
    val composeMd3Version by extra("1.0.0-rc01")
    val coilVersion by extra("2.2.2")
    val okhttpVersion by extra("5.0.0-alpha.10")
    val kotlinVersion by extra("1.7.20")
    val hiltVersion by extra("2.44")
    val rssParserVersion by extra("0.6.0")
    val exoPlayerVersion by extra("2.18.0")
    val retrofitVersion by extra("2.9.0")
    val materialVersion by extra("1.6.1")
    val appCompatVersion by extra("1.7.0-alpha01")
    val ktxCoreVersion by extra("1.9.0")
    val activityComposeVersion by extra("1.6.0")
    val composeCompilerExtensionVersion by extra("1.3.2")
    val media3Version by extra("1.0.0-beta02")
    val opmlParserVersion by extra("2.2.0")

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}
plugins {
    id("com.android.application") version "8.7.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.android.library") version "8.7.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

