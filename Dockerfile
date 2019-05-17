FROM openjdk:11-jdk
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar