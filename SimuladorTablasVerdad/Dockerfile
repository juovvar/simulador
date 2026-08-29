# Etapa de compilacion con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar simulador-app.jar

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "simulador-app.jar"]