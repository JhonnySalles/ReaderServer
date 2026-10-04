# Stage 1: Build com Maven e OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache de dependências Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código fonte e compila (respeitando .dockerignore que ignora application.yml)
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime com JRE leve
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root para execução segura
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /app/logs /app/config && chown -R appuser:appgroup /app

USER appuser

# Copia jar compilado do builder
COPY --from=builder /app/target/*.jar /app/app.jar

# Variáveis padrão
ENV PORT=8083 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
