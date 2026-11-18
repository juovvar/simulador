# compilacion con JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ejecucion con JRE 21
FROM eclipse-temurin:21-jre

# usuario sin privilegios para ejecutar la aplicacion
RUN groupadd --system app && useradd --system --gid app --no-create-home --shell /usr/sbin/nologin app

WORKDIR /app
# el jar queda propiedad de root: el proceso puede leerlo pero no modificarlo
COPY --from=build /app/target/simulador-app-1.0-SNAPSHOT-jar-with-dependencies.jar simulador-app.jar

USER app

EXPOSE 8080
# heap acotado al limite del contenedor y reinicio limpio ante falta de memoria
CMD ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "simulador-app.jar"]
