FROM maven:3.8.6-jdk-11-slim as builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM adoptopenjdk/openjdk11:jre-11.0.11_9
COPY --from=builder /usr/src/app/target/social-network-0.0.1-SNAPSHOT.jar social-network.jar
CMD java -jar social-network.jar
