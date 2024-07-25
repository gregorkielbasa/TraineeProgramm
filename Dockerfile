FROM eclipse-temurin:latest
LABEL authors="Gregor Kielbasa"

COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]