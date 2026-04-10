plugins {
    id("java")
    id("maven-publish")
}

group = "io.github.tanguygab"
version = "9.2.0"

tasks.compileJava.configure {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(libs.itemsadder)
    compileOnly(libs.papi)
    compileOnly("io.netty:netty-all:5.0.0.Alpha2")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
    compileOnly(files("../../dependencies/nms/nms-26.1.jar"))
    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filesMatching(listOf("plugin.yml")) {
        expand(props)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
