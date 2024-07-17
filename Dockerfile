FROM eclipse-temurin:latest
LABEL authors="Gregor Kielbasa"

COPY /target/phase2-1.2.jar /run/
WORKDIR /run
CMD ["java","-jar","/run/phase2-1.2.jar"]