# Spring Boot Starter - Stateless OIDC/OAuth(Single or multiple issuers), PostgreSQL, TestContainers, Liquibase, Docker
This project serves as a boilerplate template for a scalable spring boot microservice. Configured for integration testing using Postgres SQL in TestContainers as well as OAuth2/OIDC JWT authentication. This specific template is designed around using keycloak for authentication/authorization but can be changed to fit whatever need. Also comes with a base docker image, yaml for kubernetes pods, liquibase(prod and test) starter, and sql init scripts. 

The main goal of this project is to serve as a reusable base for secure microservices. If using this base, you just need to rename/restructure the project to fit your needs and plug in connection information wherever necessary. 

For security purposes, you should never store credentials inside this project - only as an environment variable or key in some other place. 

## Goals
- [x] Starter src and test structure
- [x] Boilerplate service, repository, and DTOs
- [x] Config testcontainers to use postgres for scalable integration tests
- [x] Integrate OpenAPI for documentation and contract based development
- [x] Sample database config in application.yml
- [x] Stateless Spring security JWT authentication boilerplate to support single or multiple issuers
- [x] Dockerfile for base image
- [x] Yaml config for base pod using podman/kubernetes

## How to use
1. Clone this repo into your own branch and use it to develop your own project.

```git clone (git project link)```

2. Modify project folder structure, change code, replace connection strings, and change /config files to match your use case. 

3. Start developing! 

If you use this and end up releasing, all I ask for is a reference to this project somehow. 

## Project Structure Breakdown
Below is a breakdown of the project structure, not going in depth on specific file documentation. 

```text
your_repo_name/
  pom.xml
  mvnw
  mvnw.cmd
  .mvn/wrapper/maven-wrapper.properties
  src/
    main/
      java/com/example_project_name/
        app/MyServiceApplication.java
        config/ --> auth and security config files
        controller/ --> controllers to define endpoints
          dto/ --> DTOs used by controllers
        model/ --> Jakarta/Hibernate mappings for models/entities to represent dbs
        repository/ --> contains Spring Data repositories to generate boilerplate SQL
          specification/ --> contains Spring Data specs for more complex SQL queries
        service/ --> seperated interface pattern; contains service interfaces
          serviceImpl/ --> contains associated service impls for decoupling
      resources/application.yml --> replaced regular prop file with yml, contains connection template for sql and auth config
    test/ --> same file structure as main, additional integration testing folder - unit tests handled in level above integration
```
