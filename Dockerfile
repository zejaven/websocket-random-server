FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn install -DskipTests

FROM tomcat:10.1.9-jdk17-temurin-jammy
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/websocket_random_server.war
EXPOSE 8080
