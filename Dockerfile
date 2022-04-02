FROM openjdk:11
VOLUME /tmp
RUN mkdir -p logs
COPY target/kidsclub.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
