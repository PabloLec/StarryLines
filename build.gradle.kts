import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    application
    jacoco
    id("com.apollographql.apollo3") version "3.8.2"
}

group = "dev.pablolec"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.litote.kmongo:kmongo:4.10.0")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    implementation("com.jcabi:jcabi-log:0.23.0")
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")
    implementation("com.github.sya-ri:kgit:1.0.5")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.apollographql.apollo3:apollo-testing-support:3.8.2")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
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
