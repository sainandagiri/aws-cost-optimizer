FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/aws-cost-optimizer-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]