# Dept Video / back-end-java
The backend is a Java 8 and Spring Boot application, and all the available endpoints are described using [Swagger](https://swagger.io/).
The database is implemented using mongo. 
The cache is implemented using Hazelcast.

### Pre requisites ###
- Install java JDK 8.
- Install MongoDb.

#### Installation and Getting started
Run the Gradle command to build and execute the command to start the application.
The database parameters con be overwritten to point to the proper URL.
Also, all the security keys and password are encrypted using [Jasypt](http://www.jasypt.org/), and the environments need to contain encryption password (JASYPT_ENCRYPTOR_PASSWORD)

```
./gradlew build
java -jar build/libs/dept-video-server.jar  --jasypt.encryptor.password=<secret> --spring.data.mongodb.port=37017 --spring.data.mongodb.host=localhost
```

##### Encrypted values generations
To be able to encrypt values is necessary to run this command
```
./dockercompose/jasypt-1.9.3/bin/encrypt.sh input=<Value to encript> password=<secret>
```

The environment variable for the java context needs to contains this variable.
```
JASYPT_ENCRYPTOR_PASSWORD=<secret>
```


#### Swagger endpoint
All the available are describe using Swagger on this url:
[http://localhost:1888/video-dept/swagger-ui.html](http://localhost:1888/video-dept/swagger-ui.html)


#### Test
To run the test cases:
```
./gradlew test
```