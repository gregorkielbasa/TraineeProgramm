FROM eclipse-temurin:latest
LABEL authors="Gregor Kielbasa"


COPY /target/*.jar /run/app
ENTRYPOINT ["java","-jar","/run/app.jar"]