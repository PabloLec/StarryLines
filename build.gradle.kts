import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.apollographql.apollo3").version("3.6.0")
}

group = "dev.pablolec"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.litote.kmongo:kmongo:4.7.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.slf4j:slf4j-log4j12:2.0.0")
    implementation("com.jcabi:jcabi-log:0.22.0")
    implementation("com.apollographql.apollo3:apollo-runtime:3.6.0")
    implementation("com.github.sya-ri:kgit:1.0.5")
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
