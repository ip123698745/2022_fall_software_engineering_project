FROM amazoncorretto:11-alpine-jdk

# Copy project files
COPY . /app

# Set working directory
WORKDIR /app

# Build project(Skip tests)
RUN ./gradlew build -x test

# Expose https(5005) and http(5006) port
EXPOSE 5005 5006

# Run project
CMD ["./gradlew", "bootRun"]
