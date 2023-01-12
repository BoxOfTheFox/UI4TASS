import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.7.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.google.code.gson:gson:2.10")
                implementation("io.ktor:ktor-client-core:2.2.2")
                implementation("io.ktor:ktor-client-cio:2.2.2")
                implementation("io.ktor:ktor-client-serialization:2.2.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.2.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.2")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "UI4TASS"
            packageVersion = "1.0.0"
        }
    }
}
