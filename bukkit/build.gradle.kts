dependencies {
    implementation(project(":common"))
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}