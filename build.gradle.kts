plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "gview"
version = "Proto-1"

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Use the Kotlin JDK standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0" )
    // JGit
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.6.0.201912101111-r")
    // Ikonli
    implementation( "org.kordamp.ikonli:ikonli-javafx:11.4.0")
    implementation( "org.kordamp.ikonli:ikonli-materialdesign-pack:11.4.0")
    // SLF4J/Log4J
    implementation("org.slf4j:slf4j-log4j12:1.7.30")
}

// JavaFX PlugIn
javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls","javafx.fxml")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}