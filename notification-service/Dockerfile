FROM openjdk:21-jdk-oracle

# Add wait-for-it script to wait for dependent services
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8084


# Wait for MySQL and Eureka before starting the application
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh mysql:3306 -t 60 && /wait-for-it.sh eureka-server:8761 -t 60 && java -jar app.jar"]