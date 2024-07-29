plugins {
    id("java-library")
}

dependencies {
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("org.jline:jline:3.26.3")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.json:json:20240303")
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.fusesource.jansi:jansi:2.4.1")
    implementation("nl.kyllian:PrivateBin-java:1.1-SNAPSHOT")
    implementation("io.netty:netty-all:4.1.111.Final")

    api("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    api(project(":networking"))
}