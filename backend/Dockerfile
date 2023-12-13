FROM gradle:8.2.1-jdk17 as builder
WORKDIR /app
COPY . /app/.
RUN gradle -p /app clean build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/*.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
