FROM maven:3.9.9-eclipse-temurin-8-focal as build
COPY ./src /build1/src
COPY ./pom.xml /build1
WORKDIR /build1
RUN mvn package
FROM cremuzzi/softhsm2
USER root
RUN apk add openjdk8-jre
COPY --from=build /build1/target/SoftHSM-REST.jar /app/SoftHSM-REST.jar
ADD src/test/resources/config-softhsm.yml /app/config.yml
CMD softhsm2-util --init-token --slot 0 --label "TestSlot1" --so-pin 1234567 --pin 1234567;java -jar /app/SoftHSM-REST.jar server /app/config.yml
EXPOSE 9080