# Simulador de Tablas de Verdad

Aplicación web de un simulador de tablas de verdad. El proyecto está desarrollado en **Java puro** utilizando el servidor HTTP embebido nativo del JDK y una interfaz ligera maquetada con **HTML5, CSS3 vanilla y JavaScript (ES6+)**.

---

## Tecnologías Utilizadas

* **Lenguaje:** Java 21
* **Gestor de Construcción:** Apache Maven
* **Servidor Web:** `com.sun.net.httpserver.HttpServer` (Servidor HTTP nativo de Java)
* **Frontend:** HTML5, CSS3 vanilla y JavaScript (ES6+)

---

## Estructura del Proyecto

El proyecto sigue la convención estándar de Maven:

```text
simulador-tablas-verdad/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── simulador/
        │           ├── Main.java                 # Punto de entrada y configuración del HttpServer
        │           ├── handler/
        │           │   ├── StaticFileHandler.java # Servidor de archivos estáticos (HTML, CSS, JS, PNG)
        │           │   └── TruthTableHandler.java # Endpoint REST API que procesa la petición JSON
        │           └── logic/
        │               ├── Evaluator.java         # Motor de lógica formal, RPN y validaciones
        │               └── TableGenerator.java    # Generador combinatorio y formateador de JSON
        └── resources/
            └── static/
                ├── index.html                 # Interfaz de usuario
                ├── style.css                  # Contenedores
                └── img/                       # Iconos