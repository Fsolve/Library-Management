# Stage 1: Build the application using Maven with Java 21
FROM maven:3.8.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build it
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application with a lightweight JDK
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8082

# Set the default command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
