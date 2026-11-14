# ---- Build-Stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Maven-Wrapper und pom zuerst kopieren, damit Dependencies gecacht werden
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B package -DskipTests

# ---- Runtime-Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
