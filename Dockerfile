FROM eclipse-temurin:17-jdk-alpine
# Set the working directory in the container
WORKDIR /app

# Copy the entire project directory into the container
COPY . /app

EXPOSE 8083

CMD ["java", "-jar", "build/libs/java-api-generator-0.0.1-SNAPSHOT.jar"]