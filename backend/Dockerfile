# -------- BUILD --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -DskipTests quarkus:build

# -------- RUNTIME --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Quarkus fast-jar (gera /target/quarkus-app)
COPY --from=build /build/target/quarkus-app/ /app/quarkus-app/

EXPOSE 8080
CMD ["java","-jar","/app/quarkus-app/quarkus-run.jar"]
