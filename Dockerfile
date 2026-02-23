FROM eclipse-temurin:17-jre-jammy

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/legend-versions-dashboard-0.1.0-SNAPSHOT.jar /app/app.jar
COPY config.yml /app/config.yml

EXPOSE 8080 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8081/healthcheck || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar", "server", "/app/config.yml"]
