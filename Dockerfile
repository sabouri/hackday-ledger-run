FROM openjdk:11-jdk
COPY target/ledger-*.jar app.jar
ENTRYPOINT java -Dserver.port=${PORT} -jar /app.jar
