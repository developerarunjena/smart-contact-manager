# Pull base image 
From tomcat:8-jre8 

# Maintainer 
MAINTAINER "devarunjena@gmail.com"
COPY ./smart-contact-manager.war /usr/local/tomcat/webapps