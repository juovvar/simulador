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
        │           │   ├── RateLimitHandlerDecorator.java
        │           │   ├── SecurityHandlerDecorator.java
        │           │   ├── StaticFileHandler.java
        │           │   └── TruthTableHandler.java
        │           ├── logic/
        │           │   ├── Evaluator.java
        │           │   └── TableGenerator.java
        │           └── util/
        │               ├── HttpResponses.java
        │               └── Json.java
        └── resources/
            └── static/
                ├── index.html                 
                ├── style.css
                ├── app.js                 
                └── img/                       
```

---

## Configuración

Variables de entorno opcionales:

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `PORT` | `8080` | Puerto HTTP (1-65535). |
| `RATE_LIMIT_PER_MINUTE` | `120` | Peticiones por minuto y por cliente permitidas en `/api/evaluar`. |
| `TRUST_PROXY` | `false` | Con `true`, identifica al cliente por la última IP de `X-Forwarded-For`. Actívalo solo detrás de un proxy inverso confiable. |

Los tiempos máximos del servidor HTTP se pueden ajustar con `-Dsun.net.httpserver.maxReqTime=<segundos>` y `-Dsun.net.httpserver.maxRspTime=<segundos>` (10 s por defecto).
