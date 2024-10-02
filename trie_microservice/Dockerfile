# Use base java 21 image
FROM openjdk:21-slim

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/typeahead-app.jar /app/typeahead-app.jar

# Expose port app runs on
EXPOSE 8080

# Command to run app
ENTRYPOINT ["java", "-jar", "typeahead-app.jar"]