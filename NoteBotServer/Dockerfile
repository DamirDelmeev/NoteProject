FROM openjdk:latest
ADD target/notebot-server-docker.jar notebot-server-docker.jar
EXPOSE 6000
CMD ["java","NoteBotServerApplication.java"]
ENTRYPOINT ["java","-jar","notebot-server-docker.jar"]

