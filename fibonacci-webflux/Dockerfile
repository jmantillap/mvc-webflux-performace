FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/fibonacci-webflux-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8787
ENTRYPOINT ["java", "-jar", "app.jar"]