# Etapa 1: Construcción (Build) usando Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos primero el pom.xml para descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run) usando una imagen ligera de Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto estándar
EXPOSE 8080

# Ejecutamos forzando el perfil de producción (application-prod.properties)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
