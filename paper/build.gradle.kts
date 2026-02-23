dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
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