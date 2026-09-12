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
SimuladorTablasVerdad/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── simulador/
        │           ├── Main.java                
        │           ├── handler/
        │           │   ├── SecurityHandlerDecorator.java
        │           │   |── StaticFileHandler.java 
        |           |   └── TruthTableHandler.java  
        │           └── logic/
        │               ├── Evaluator.java        
        │               └── TableGenerator.java    
        └── resources/
            └── static/
                ├── index.html                 
                ├── style.css
                ├── app.js                 
                └── img/                       
