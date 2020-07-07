# Dept Video

Dept video is an application to search for video information and trailers visualization. 
The backend is a Java 8 and Spring Boot application, and all the available endpoints are described using Swagger
The UI is made base on React Js.

## Installation and Getting started

This section provides a high-level requirement.

### Pre requisites
[Docker](https://docs.docker.com). 

[npmjs](https://www.npmjs.com/). 
 
#### Installation
Run the command to generate all the artifacts and start the docker-compose.
```
chmod +x dockerbuild.sh 
./dockerbuild.sh
```
 
#### Application url ####
One the docker start and all the services are on these URLs will be available:
 
 - UI [http://localhost:1880/](http://localhost:1880/)
 - Backend Swagger [http://localhost:1888/v1/dept-video/swagger-ui.html](http://localhost:1888/v1/dept-video/swagger-ui.html)

The access on the application is restricted by user. By default the applications creates the admin user:

#### Default user and password
 
**username**: admin@video.dept.com 

**password**: DeptPasw001.


### Main technologies
 
 Deployment:
 
 - Docker
 - Docker compose
 - Nginx
 
 Backend:
 - Java 1.8
 - Spring Boot v2.1.6.RELEASE
 - MongoDb
 - Hazelcast Cache
 - Swagger 
 - Jasypt 
 - JWT
 
 
 External Api:
 - https://www.themoviedb.org
 - https://youtube.com
 
 
 UI
 - React Js
 - Ant design
 - Lazy load


### Adding users
Authenticated as admin is possible to add more users to the application on this page:
[http://localhost:1880/management/users](http://localhost:1880/management/users)