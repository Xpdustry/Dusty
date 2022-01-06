import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "fr.xpdustry"
version = "1.2"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }

    jar {
        manifest { attributes["Main-Class"] = "fr.xpdustry.dusty.DustyKt" }
    }

    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    // JDA
    implementation("net.dv8tion:JDA:5.0.0-alpha.2") {
        exclude("club.minnced", "opus-java")
    }

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    // Commands
    implementation("cloud.commandframework:cloud-core:1.6.1")
    implementation("cloud.commandframework:cloud-jda:1.6.1")
    implementation("cloud.commandframework:cloud-kotlin-extensions:1.6.1")

    // Annotations
    implementation("org.jetbrains:annotations:23.0.0")

    // Helper
    implementation("com.google.code.gson:gson:2.8.9")
}
