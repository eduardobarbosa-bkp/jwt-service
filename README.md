# JWT service (spring boot application) - Eduardo Barbosa da Costa

***Setup***
* docker, kubernetes and maven;

***Run***
##### On the command line on the project root:

1. *./mvnw install dockerfile:build or ./mvnw install && docker build -t gcr.io/scratch-microservice/jwt-service:v1 --build-arg JAR_FILE=target/jwt-service-0.0.1-SNAPSHOT.jar .*
2. *docker-compose up -d*
The endpoints will be available in: http://&lt;docker host&gt;:8080

##### On kubernetes
2. *kubectl create -f .*
The endpoints will be available in: http://&lt;cluster IP&gt;:8080