FROM openjdk:latest
ADD target/notebot-client.jar notebot-client.jar
EXPOSE 5000
CMD ["java","NoteBotApplication.java"]
ENTRYPOINT ["java","-jar","notebot-client.jar"]