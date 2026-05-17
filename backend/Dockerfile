# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Cache de dependencias: copia solo pom.xml primero
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente y compila el jar
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Instala curl para healthcheck y crea usuario sin privilegios
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/* \
    && addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-Xms256m", "-Xmx512m", "-XX:+UseSerialGC", "-Dspring.jpa.open-in-view=false", "-jar", "app.jar"]
