import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "fr.xpdustry"
version = "0.1"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }

    test {
        useJUnitPlatform()
    }

    /* For some reasons, a class is missing...
    register<Jar>("fatJar") {
        dependsOn(jar)

        archiveBaseName.set("Dusty")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes["Main-Class"] = "fr.xpdustry.dusty.DustyKt" }

        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get())
    }
     */
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.incendo.org/content/repositories/snapshots") }
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

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
    // implementation("cloud.commandframework:cloud-kotlin-coroutines:1.6.1")

    // Annotations
    implementation("org.jetbrains:annotations:23.0.0")

    // Helper
    implementation("com.google.code.gson:gson:2.8.9")
}

application {
    mainClass.set("fr.xpdustry.dusty.DustyKt")
}