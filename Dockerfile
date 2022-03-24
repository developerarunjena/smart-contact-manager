FROM tomcat:8
COPY target/*.war /usr/share/local/tomcat/webapps/
