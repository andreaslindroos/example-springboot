FROM openjdk:11-jre-slim
WORKDIR /project
ADD ./service/target/*.jar teams-service.jar
CMD java -jar teams-service.jar