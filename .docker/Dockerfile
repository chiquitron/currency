FROM java:7
MAINTAINER veronica.otero@balidea.com
cd /home/travis/build/chiquitron/currency/target
ADD currencyExchange.jar /data/currencyExchange/
EXPOSE 8080
WORKDIR /data/brfClientCenter/
CMD ["java", "-jar", "currencyExchange.jar"]

