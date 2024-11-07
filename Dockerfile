FROM openjdk:19-jdk
EXPOSE 8090
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} venga-business-ms.jar
ENTRYPOINT ["java","-jar","/venga-business-ms.jar"]
