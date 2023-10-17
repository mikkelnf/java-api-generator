FROM eclipse-temurin:17-jdk-alpine
# Set the working directory in the container
WORKDIR /app

# Copy the entire project directory into the container
COPY . /app

# Expose the port your application will listen on
EXPOSE 8080

# Command to run the Spring Boot application in production mode using wildcard
CMD ["java", "-jar", "build/libs/java-api-generator-0.0.1-SNAPSHOT.jar"]