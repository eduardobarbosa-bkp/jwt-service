# JWT service (spring boot application) - Eduardo Barbosa da Costa

***Setup***
* docker, kubernetes and maven;

***Run***
* On the command line on the project root:

1. *./mvnw install dockerfile:build or ./mvnw install && docker build -t eduardobarbosa/jwt-service:1.0 --build-arg JAR_FILE=target/jwt-service-0.0.1-SNAPSHOT.jar .*
2. *docker-compose up -d*
The endpoints will be available in: http://<docker host>:8080

* On kubernetes
2. *kubectl create -f .*
The endpoints will be available in: http://<cluster IP>:8080