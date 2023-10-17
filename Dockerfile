FROM eclipse-temurin:17-jdk-alpine
# Set the working directory in the container
WORKDIR /app

# Copy the entire project directory into the container
COPY . /app

# Build the Spring Boot application using Gradle (or your build tool)
RUN ./gradlew build

# Expose the port your application will listen on
EXPOSE 8080

# Command to run the Spring Boot application in production mode
CMD ["java", "-jar", "build/libs/*.jar"]