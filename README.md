# Spring Boot Starter - OIDC/OAuth + PostgreSQL 
This project serves as a starter template for spring boot projects. It comes with a base configuration for integration with Postgres SQL as well as OAuth2/OIDC authentication. This specific template is designed around using keycloak for authentication/authorization but can be changed to fit whatever purpose. 

The main goal of this project is to serve as a reusable base for secure, scalable microservices. If using this base, you just need to rename/restructure the project to fit your needs and plug in connection information wherever necessary. 

For security purposes, you should never store credentials inside this project - only as an environment variable or key in some other place. 

## Goals
- [x] Starter project structure
- [ ] Testing structure
- [ ] Config for integration and unit testing
- [ ] Database config integration
- [ ] Authentication config integration
- [ ] Launch file starter
- [ ] Dockerfile for base image
- [ ] Yaml config for base kubernetes pod

## How to use
Clone this repo into your own branch and use it to develop your own project.
- 'git clone (git project link)'

1. Modify project folder structure names and file names to match your use case. 

2. Ensure the project is using your connection values by changing them in the .launch file. I recommend using env vars or something similar and referencing them in your launch file instead of storing raw connection data. 

3. Make changes to /config files, dockerfile, and kubernetes yaml if they don't fit your use case. 

4. Make your project! 

If you use this and end up releasing, all I ask for is a reference to this project somehow. 
