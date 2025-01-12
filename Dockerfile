FROM openjdk:17
EXPOSE 8080
ADD order-0.0.1-SNAPSHOT.jar order-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","order-0.0.1-SNAPSHOT.jar","> logs/log.out &"]