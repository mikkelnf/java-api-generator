FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container (replace "your-app.jar" with your actual JAR file name)
COPY target/your-app.jar /app/app.jar

# Expose the port your application will listen on
EXPOSE 8080

# Define the command to run your application when the container starts
CMD ["java", "-jar", "app.jar"]