FROM openjdk:11-jre-slim
ENV JAVA_OPTS=""
COPY ./target/demo-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 8080
CMD java ${JAVA_OPTS} -jar /usr/local/lib/demo.jar