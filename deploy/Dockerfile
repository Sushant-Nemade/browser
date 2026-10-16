# ==============================================================================
# Multi-Stage Dockerfile for Realm Stitchers Backend
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage with Maven & Eclipse Temurin JDK 21
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Pre-cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build production JAR package
COPY src ./src
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Hardened Runtime Container with Eclipse Temurin JRE 21 Alpine
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Install wget for container healthcheck
RUN apk add --no-cache wget ca-certificates

# Create unprivileged non-root user and group
RUN addgroup -S -g 10001 appgroup && \
    adduser -S -u 10001 -G appgroup -h /app -s /sbin/nologin appuser

# Copy built artifact from builder stage
COPY --from=builder /workspace/target/realm-stitchers-1.0.0.jar app.jar

# Assign ownership to unprivileged user
RUN chown -R appuser:appgroup /app

# Switch to non-root execution context
USER appuser:appgroup

# Expose internal application port
EXPOSE 8080

# Production container healthcheck probe
HEALTHCHECK --interval=15s --timeout=5s --start-period=20s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/argon/health || exit 1

# Container JVM optimization flags
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
