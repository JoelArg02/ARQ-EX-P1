# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de proyecto
COPY pom.xml .
COPY src ./src

# Compilar proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM payara/server-full:6.2023.12-jdk17

# Instalar netcat para verificar conectividad con MySQL
USER root
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*
USER payara

# Copiar el WAR generado
COPY --from=build /app/target/*.war $DEPLOY_DIR/

# Copiar script de configuración con permisos de ejecución
COPY --chmod=755 docker-setup.sh /opt/payara/docker-setup.sh

# Exponer puerto
EXPOSE 8081 4849

# Configurar datasource al iniciar
ENTRYPOINT ["/opt/payara/docker-setup.sh"]
