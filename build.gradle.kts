@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "1.6.21"                                //NON-NLS
    kotlin("plugin.serialization") version "1.6.21"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "gview"                   //NON-NLS
version = "Proto-1"               //NON-NLS

repositories { mavenCentral() }

dependencies {
    // Use the Kotlin JDK standard library.
    implementation(kotlin("stdlib-jdk8"))                                         //NON-NLS
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")      //NON-NLS
    // JGit
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r")      //NON-NLS
    // SLF4J/Log4J
    implementation("org.slf4j:slf4j-log4j12:2.0.0")                              //NON-NLS
    // Ikonli
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")                     //NON-NLS
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1")       //NON-NLS
    //ControlFX
    implementation("org.controlsfx:controlsfx:11.1.1")                            //NON-NLS

    implementation("org.jetbrains:annotations:23.0.0")                            //NON-NLS
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