## Pull base image
#FROM tomcat:8-jre8
#
## Maintainer
#MAINTAINER "devarunjena@gmail.com"
#COPY ./target/smart-contact-manager.war /usr/local/tomcat/webapps
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/smart-contact-manager.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]

EXPOSE 5000