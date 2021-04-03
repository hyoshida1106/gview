plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "gview"
version = "Proto-1"

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK standard library.
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    // JGit
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")

    // SLF4J/Log4J
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    // Ikonli
    implementation( "org.kordamp.ikonli:ikonli-javafx:11.4.0")
    implementation( "org.kordamp.ikonli:ikonli-materialdesign-pack:11.4.0")

    //ControlFX
    implementation("org.controlsfx:controlsfx:11.0.2")
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