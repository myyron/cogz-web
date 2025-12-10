# Use a base image with Java installed
FROM openjdk:23-ea-slim

# Set the working directory inside the container
WORKDIR /app
RUN mkdir logs
RUN mkdir data

# Copy the application JAR file into the container
COPY target/cogz-web-*.jar /app/cogz-web.jar

# Expose the port your application listens on
EXPOSE 443

# Command to run your application when the container starts
ENTRYPOINT ["java", "-jar", "cogz-web.jar"]