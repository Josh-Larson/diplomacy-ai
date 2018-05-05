FROM openjdk:8-slim
WORKDIR /app
ADD . /app
RUN ./gradlew assemble
CMD ["./gradlew", "run"]
