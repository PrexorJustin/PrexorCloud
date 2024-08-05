import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    compileOnly(project(":driver"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("runnable-manager")
        archiveVersion.set("")
        archiveClassifier.set("")

        manifest {
            attributes["Main-Class"] = "me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager"
        }
    }

    named("build") {
        dependsOn("shadowJar")
    }
}