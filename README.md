# Simulador de Tablas de Verdad

Aplicación web de un simulador de tablas de verdad. El proyecto está hecho en **Java puro** (sin Spring) utilizando un servidor HTTP embebido nativo y esta maquetado con **HTML/CSS puro**.


## Tecnologías Utilizadas

* **Lenguaje:** Java 21
* **Gestor de Construcción:** Apache Maven
* **Servidor Web:** `com.sun.net.httpserver.HttpServer` (Servidor HTTP nativo de Java)
* **Frontend:** HTML5, CSS3 vanilla y JavaScript (ES6+)

---

## Estructura del Proyecto

El proyecto sigue la convención estándar de carpetas de Maven:

```text
simulador-tablas-verdad/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │        └── simulador/
        │            ├── Main.java                 # Punto de entrada y servidor HTTP
        │            ├── handler/
        │            │   └── StaticFileHandler.java # Servidor de archivos estaticos (HTML, CSS, JS)
        │            └── logic/
        │                   └── Evaluator.java         # Motor de logica formal y evaluacion
        └── resources/
            └── static/
                ├── index.html                        # Interfaz de usuario de una sola pagina
                ├── style.css                         # Hoja de estilos principal
                └── img/                              # Iconos

