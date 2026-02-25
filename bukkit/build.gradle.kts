import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":common"))

    implementation("org.bstats:bstats-bukkit:3.2.1")

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
}


tasks.withType<ShadowJar> {
    archiveClassifier.set("") // replaces normal jar
    archiveBaseName.set("spycord-bukkit")

    relocate("org.bstats", "com.myst1cs04p.bstats")

    // Do NOT include Spigot API in shaded jar
    dependencies {
        exclude(dependency("org.spigotmc:spigot-api"))
    }

    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}