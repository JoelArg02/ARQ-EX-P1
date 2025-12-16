FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM payara/server-full:6.2023.12-jdk17
COPY --from=build /app/target/*.war $DEPLOY_DIR/
COPY --chmod=755 docker-setup.sh /opt/payara/docker-setup.sh
EXPOSE 8082 4850
ENTRYPOINT ["/opt/payara/docker-setup.sh"]
