FROM openjdk:8-jdk

#VOLUME /tmp
ADD  dept-video-server.jar  /home/app.jar

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/app.jar $APP_OPTS