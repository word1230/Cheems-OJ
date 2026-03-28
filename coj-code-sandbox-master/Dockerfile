FROM maven:3.8.1-jdk-8-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM openjdk:8-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]
