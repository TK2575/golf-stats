FROM amazoncorretto:21-alpine-jdk
LABEL org.opencontainers.image.authors="https://github.com/TK2575"
COPY target/golf-stats-0.0.1-SNAPSHOT.jar golf-stats-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/golf-stats-0.0.1-SNAPSHOT.jar"]