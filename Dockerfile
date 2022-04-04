FROM openjdk:11
VOLUME /tmp
RUN mkdir -p logs
COPY target/kidsclub.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx64m","-jar","/app.jar","--spring.profiles.active=prod"]
