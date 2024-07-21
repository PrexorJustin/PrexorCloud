plugins {
    id("java")
    id("io.freefair.lombok") version "8.6" apply false
}

allprojects {
    group = "me.prexorjustin.prexornetwork"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
        mavenLocal()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}



