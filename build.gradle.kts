import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "com.ec"
version = "0.0.1"

val kotlinVersion = "1.5.10"

plugins {
    java
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=compatibility")
}

repositories {
    mavenCentral()
    maven { url = URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = URI.create("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = URI.create("https://repo.codemc.org/repository/maven-public/") }
    maven { url = URI.create("https://jitpack.io") }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))

    compileOnly("dev.reactant:reactant:0.2.3")
    compileOnly("dev.reactant:resquare:0.0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.Oskang09:UniversalGUI:v3.0.0")
    compileOnly("com.github.PlaceholderAPI:PlaceholderAPI:2.10.9")
    compileOnly(fileTree("src/main/libs"))

    shadow("net.oneandone.reflections8:reflections8:0.11.5")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0") {
        exclude(group = "org.jetbrains.kotlin", module = "*")
    }
}


val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

    val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {
        configurations = listOf(project.configurations.shadow.get())
    }

    val deployPath: String by project
    val deployPlugin by tasks.registering(Copy::class) {
        dependsOn(shadowJar)

        System.getenv("PLUGIN_DEPLOY_PATH")?.let {
            from(shadowJar)
            into(it)
        }
    }

val build = (tasks["build"] as Task).apply {
    arrayOf(
            sourcesJar
                , shadowJar
                , deployPlugin
    ).forEach { dependsOn(it) }
}