FROM maven:3.8-openjdk-17-slim

WORKDIR /app

COPY pom.xml /app/

RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean package

EXPOSE 8081

CMD ["java", "-jar", "target/card_system-0.0.1-SNAPSHOT.jar"]