# AndaTech 

Desarrollo de un dispositivo inteligente IoT, diseñado para ser acoplado principalmente a andadores. El objetivo principal es monitorizar algunos aspectos del estado del usuario y andador para ofrecer tranquilidad a familiares o cuidadores, todo esto a través de una aplicación móvil en tiempo real comunicada con el dispositivo.

---

## Estructura del Repositorio

El repositorio está organizado en las siguientes carpetas para separar el software del hardware:

*   **AndaTech_App:** Código de la aplicación móvil desarrollado en Android Studio.
*   **AndaTech_Arduino:** Código(.ino) y configuraciones para el microcontrolador.
```text
AndaTech/                 <-- Carpeta raíz (aquí inicializas Git)
├── .git/                     <-- Carpeta oculta de Git (control de versiones)
├── .gitignore                <-- Filtro unificado de archivos basura
├── README.md                 <-- Guía del proyecto
│
├── AndaTech_Arduino/         <-- PROYECTO ARDUINO
│   └── AndaTech_Arduino.ino      <-- Sketch principal
│
└── AndaTech_App/             <-- PROYECTO ANDROID STUDIO
    ├── .gitignore                <-- Gitignore específico de Android
    ├── build.gradle.kts          <-- Archivo de construcción del proyecto
    ├── settings.gradle.kts       <-- Configuración de módulos de Android
    └── app/                      <-- Módulo principal de la aplicación
        ├── .gitignore
        ├── build.gradle.kts
        ├── google-services.json 
        └── src/                  <-- Tu código y diseño
            ├── main/
            │   ├── AndroidManifest.xml               <-- Permisos de la App
            │   ├── java/com/example/andatech/        <-- Código Java
            |   │   └── LoginActivity.java                <-- Login con Firebase
            |   │   └── MainActivity.java                 <-- Página principal de la app
            │   └── res/
            │       ├── layout/                       <-- Diseños Visuales (.xml)
            |       │   └── activity_main.xml             <-- Diseños página principal de la app
            |       │   └── tarjeta_andador.xml           <-- tarjeta para RecyclerView
            │       ├── drawable/                     <-- Gráficos e Imágenes
            │       └── values/                       <-- Valores Estáticos
            └── test/
```
