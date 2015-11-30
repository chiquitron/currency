FROM java:7
MAINTAINER veronica.otero@balidea.com
ADD /home/travis/build/chiquitron/currency/target/currencyExchange.jar /data/currencyExchange/
EXPOSE 8080
WORKDIR /data/brfClientCenter/
CMD ["java", "-jar", "/home/travis/build/chiquitron/currency/target/currencyExchange.jar"]

