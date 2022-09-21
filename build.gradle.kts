import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.apollographql.apollo3") version "3.6.0"
    jacoco
}

group = "dev.pablolec"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.litote.kmongo:kmongo:4.7.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.0")
    implementation("org.slf4j:slf4j-log4j12:2.0.2")
    implementation("com.jcabi:jcabi-log:0.22.0")
    implementation("com.apollographql.apollo3:apollo-runtime:3.6.0")
    implementation("com.github.sya-ri:kgit:1.0.5")
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testImplementation("io.mockk:mockk:1.12.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacoco"))
    }
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

val excluded = setOf(
    "dev/pablolec/starrylines/type/",
    "dev/pablolec/starrylines/adapter/",
    "dev/pablolec/starrylines/selections/",
    "dev/pablolec/starrylines/*Query*"
)

tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal(0.62)
            }
        }
    }

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
