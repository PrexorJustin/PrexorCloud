import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java-library")
}

repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")

        content {
            includeGroup("com.velocitypowered")
        }
    }

    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-20240729.211617-83")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1")

    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:4.17.0")
    api("net.kyori:adventure-platform-bungeecord:4.3.3")

    implementation("org.json:json:20240303")

    api(project(":driver"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("prexorcloud-api")
        archiveVersion.set("")
        archiveClassifier.set("")
    }

    named("build") {
        dependsOn("shadowJar")
    }
}