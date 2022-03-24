# Pull base image 
FROM tomcat:8-jre8

# Maintainer 
MAINTAINER "devarunjena@gmail.com"
COPY ./target/smart-contact-manager.war /usr/local/tomcat/webapps