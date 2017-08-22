FROM openjdk:8-jre-alpine

LABEL maintainer="elastest-users@googlegroups.com"
LABEL version="0.1"
LABEL description="Builds the emp docker image."

COPY target/sentinel-0.1.jar /emp.jar
COPY application.properties /application.properties
CMD java -jar /emp.jar
