plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // SDK oficial de Firebase para servidores/Raspberry Pi
    implementation("com.google.firebase:firebase-admin:9.2.0")
    // Cliente MQTT para escuchar al M5Stack
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    // Librería para leer el JSON del M5Stack
    implementation("org.json:json:20231013")
}

// Tarea para generar un único archivo .jar con todas las librerías incluidas
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.raspberry.PuenteMqttFirebase"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
}
