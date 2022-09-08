import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.apollographql.apollo3").version("3.5.0")
}

group = "dev.pablolec"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // The core runtime dependencies
    implementation("com.apollographql.apollo3:apollo-runtime:3.5.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

apollo {
    generateKotlinModels.set(true)
    packageName.set("dev.pablolec.starrylines")
}