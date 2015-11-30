FROM java:7
MAINTAINER veronica.otero@balidea.com

# Install maven
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /data/currencyExchange/

# Prepare by downloading dependencies
ADD pom.xml /data/currencyExchange/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /data/currencyExchange/src
RUN ["mvn", "package"]
RUN ["mvn", "test"]

EXPOSE 8080

CMD ["java", "-jar", "currencyExchange.jar"]

