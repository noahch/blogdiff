FROM openjdk:8-alpine

EXPOSE 8080
WORKDIR /usr/src/app
COPY target/BLogDiff.jar .

CMD ["java", "-jar", "/usr/src/app/BLogDiff.jar"]