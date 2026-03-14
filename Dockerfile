# Use the official OpenJDK 17 image as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the source code into the container
COPY src/Main.java .
COPY quotes.txt .
COPY index.html .

# Compile the Java code
RUN javac Main.java

# Expose port 8000 for the HTTP server
EXPOSE 8000

# Run the Java application when the container starts
CMD ["java", "Main"]
