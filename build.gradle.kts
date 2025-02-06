import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "dev.kumchatka"
version = "1.2.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    implementation("de.u-mass:lastfm-java:0.1.2")

//    implementation("com.github.vpaliyX:Last.fm-API:v1.2.0")
//    implementation("com.google.code.gson:gson:2.8.9")
//    implementation("com.squareup.okio:okio:3.4.0")
//    implementation("com.google.code.gson:gson:2.8.9")
//    implementation("com.squareup.retrofit2:retrofit:2.5.0")
//    implementation("com.squareup.okio:okio:3.4.0")

    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("com.natpryce:konfig:1.6.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

application {
    mainClass.set("MainKt")
}
