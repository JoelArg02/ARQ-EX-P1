FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM payara/server-full:6.2023.12-jdk17
COPY --from=build /app/target/*.war $DEPLOY_DIR/
COPY docker-setup.sh /opt/payara/docker-setup.sh

# Convertir finales de línea de Windows a Unix y dar permisos
USER root
RUN sed -i 's/\r$//' /opt/payara/docker-setup.sh && chmod +x /opt/payara/docker-setup.sh
USER payara

EXPOSE 8082 4850
ENTRYPOINT ["/opt/payara/docker-setup.sh"]
