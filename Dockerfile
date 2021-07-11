FROM openjdk:8-jre-alpine
WORKDIR /backend
COPY ./target/MockScenerioSupplier-1.0-SNAPSHOT.jar ./target/MockScenerioSupplier-1.0-SNAPSHOT.jar
COPY ./src/main/resources ./src/main/resources
CMD ["java", "-jar", "target/MockScenerioSupplier-1.0-SNAPSHOT.jar", "server", "config.yml"]