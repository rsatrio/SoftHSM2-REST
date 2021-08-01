FROM cremuzzi/softhsm2
USER root
RUN apk add openjdk8-jre
ADD target/SoftHSM-REST.jar /app/SoftHSM-REST.jar
ADD src/test/resources/config-softhsm.yml /app/config.yml
CMD java -jar /app/SoftHSM-REST.jar server /app/config.yml
EXPOSE 9080