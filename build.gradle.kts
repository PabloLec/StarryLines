import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
    jacoco
    id("com.apollographql.apollo3") version "3.7.4"
}

group = "dev.pablolec"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.litote.kmongo:kmongo:4.8.0")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-log4j12:2.0.6")
    implementation("com.jcabi:jcabi-log:0.22.0")
    implementation("com.apollographql.apollo3:apollo-runtime:3.7.4")
    implementation("com.github.sya-ri:kgit:1.0.5")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("com.apollographql.apollo3:apollo-testing-support:3.7.4")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("cli.InterfaceKt")
}

apollo {
    generateKotlinModels.set(true)
    packageName.set("dev.pablolec.starrylines")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

val excluded = setOf(
    "dev/pablolec/starrylines/type/",
    "dev/pablolec/starrylines/adapter/",
    "dev/pablolec/starrylines/selections/",
    "dev/pablolec/starrylines/*Query*",
    "cli/Interface*",
    "models/SupportedLanguage*"
)

tasks.withType<JacocoCoverageVerification> {
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it).apply {
                        exclude(excluded)
                    }
                }
            )
        )
    }
}

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it).apply {
                        exclude(excluded)
                    }
                }
            )
        )
    }
}