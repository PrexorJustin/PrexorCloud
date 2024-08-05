plugins {
    id("java-library")
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation("org.slf4j:slf4j-nop:2.0.13")

    api("net.kyori:adventure-api:4.17.0")
    api("io.netty:netty-all:4.1.111.Final")

}