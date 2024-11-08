# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add wait-for-it script
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh mysql:3306 -t 60 && /wait-for-it.sh eureka-server:8761 -t 60 && java -jar app.jar"]
