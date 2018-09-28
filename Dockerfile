FROM openjdk:alpine

RUN apk --update --no-cache add tar && rm -rf /var/cache/apk/*

ENV APP_HOME /app/

COPY target/performance-tests*-jar-with-dependencies.jar $APP_HOME/performance-tests-jar-with-dependencies.jar

WORKDIR $APP_HOME
CMD java -jar performance-tests-jar-with-dependencies.jar