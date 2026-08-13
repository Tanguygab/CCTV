plugins {
    id("java")
    id("maven-publish")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "io.github.tanguygab"
version = "9.3.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(26))
}

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
    paperweight.paperDevBundle("26.2.build.+")
    compileOnly(libs.papi)
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
