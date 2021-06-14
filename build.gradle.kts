import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "com.ec"
version = ""

val kotlinVersion = "1.5.10"

plugins {
    java
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
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
    maven { url = URI.create("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = URI.create("https://jitpack.io") }
    maven { url = URI.create("https://m2.dv8tion.net/releases") }
    maven { url = URI.create("https://repo.citizensnpcs.co") }
    maven { url = URI.create("https://repo.minebench.de") }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
    testImplementation(kotlin("test"))

    compileOnly("dev.reactant:reactant:0.2.3")
    compileOnly("dev.reactant:resquare:0.0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.PlaceholderAPI:PlaceholderAPI:2.10.9")
    compileOnly("com.github.Oskang09:UniversalGUI:3.0.4")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    compileOnly("net.citizensnpcs:citizensapi:2.0.27-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.8.0")
    compileOnly(fileTree("src/main/libs"))

    implementation("org.jetbrains.exposed:exposed-core:0.32.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.32.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.32.1")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("io.javalin:javalin:3.13.7")

    implementation("com.github.WesJD:AnvilGUI:-SNAPSHOT")
    implementation("de.themoep:minedown:1.7.1-SNAPSHOT")
    implementation("xyz.xenondevs:particle:1.5.1")
    implementation("net.oneandone.reflections8:reflections8:0.11.5")

    compileOnly("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.github.Oskang09:RM-API-SDK-KOTLIN:0.0.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("com.github.MinnDevelopment:jda-reactor:1.3.0")
    implementation("net.dv8tion:JDA:4.2.1_253") {
        exclude(module = "opus-java")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    manifest {
        attributes(mapOf("Main-Class" to "$group/ECCore"))
    }
}

val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {
    exclude("kotlin/**")
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