FROM openjdk:21-jdk AS java
WORKDIR /app
COPY ./server /app
RUN ./mvnw dependency:go-offline
EXPOSE 8080
ENTRYPOINT ["./mvnw", "spring-boot:run"]

#FROM openjdk:21-jdk as prod
#COPY --from=java /usr/src/app/target/*.jar ./uftos.jar
#ENTRYPOINT ["java", "-jar", "uftos.jar"]
