#
# Build stage
#
FROM maven:3.8.5-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -B package -e -X --file /home/app/pom.xml

#
# Package stage
#
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /home/app/target/test-1.0-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]