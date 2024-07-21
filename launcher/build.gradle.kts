import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":driver"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("PrexorCloudLauncher")
        archiveVersion.set("")
        archiveClassifier.set("")

        manifest {
            attributes["Main-Class"] = "me.prexorjustin.prexornetwork.cloud.launcher.PrexorCloudBoot"
        }
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
