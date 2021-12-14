import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    kotlin("plugin.serialization") version "1.5.21"
}

group = "ru.nsu_null"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.antlr:antlr4:4.9")
    api(compose.materialIconsExtended)
    implementation("com.charleskorn.kaml:kaml:0.37.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.fifesoft:rsyntaxtextarea:3.1.3")
    implementation("me.tomassetti.kanvas:kanvas-core:0.2.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "NPidE"
            packageVersion = "1.0.0"
        }
    }
}