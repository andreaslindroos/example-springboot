# Example Spring Boot project that uses various libraries

### Disclaimer

- This is not the best design and design choices was made based on needs. I use it here to demonstrate certain tools and
  how they work together.
- The versions used for the tools version are a bit outdated, but the tools still exists in some form as newer versions.
- Original code was written 2019

### Service Background

The service itself handles teams and users in teams. You can browse/create teams, invite team members. The users are
just UUID ids, so it holds no user info. The users are meant to be stored in another services.

Some endpoints require JWT tokens. Those can be generated using the token-creator endpoint (see swagger UI). The
UUID for an existing user can be found using the team endpoints.

The service uses an H2 internal DB locally, so it can be booted up easily. Postgres is used in "ci" (test env) profile.

### Maven Structure
This project uses Maven modules and builds two .jar files when parent is built:
- api module: Contains OpenFeign models (see below for more info on OpenFeign) that can be used by other services. **Note**: These are translated into regular json/HTTP traffic when transmitted and not a requirement for other services calling us (optional use). 
- service module: The actual app, that contains rest controllers, service, etc. This is package into a docker container and shipped to running env. 

## Useful URLS

After starting the service, you can access these URLs to explore:

- Swagger UI: http://localhost:8081/swagger-ui.html#/
- h2 db access: http://localhost:8081/h2-console/login.do (You might have to change URL to `jdbc:h2:file:./testdb;DB_CLOSE_ON_EXIT=FALSE` in the UI on login)

## Technologies demonstrated

This section talks about different tools used in the projects highlighting interesting points.

There is no way to cover it all, so there are just interesting highlights that you might usually come across.

### JPA

Databases stuff are found in package:`eu.lindroos.taas.teams.service.service.database` with properties
in `application.yml`

JPA in this project is used to deploy the db model. In real life, we would use flyway and have JPA `validate` the models
on startup (if e.g. a field is missing, it will complain, etc.).

We define the models according to the DB structure: package `eu.lindroos.taas.teams.service.service.database.model`)

With models in place, we can use Java interfaces to interact with the database (
e.g. `eu.lindroos.taas.teams.service.service.database.TeamRepository`).

We also get optimistic database locking by adding a number field with `@Version`
annotation: `eu.lindroos.taas.teams.service.service.database.model.Team`

### OpenFeign

OpenFeign is a framework for creating HTTP clients in Java, without having to create code that parses results manually
etc.

It can be used in several ways:

- Easily integrate a new service endpoint: `eu.lindroos.taas.teams.service.service.external.ImageApi`
    1. The library is imported from a artifact repo (simulated by the "lib" folder in this project).
    2. Library contains models and interface that comes from the original endpoints from the other service.
    3. We reuse the model and interface to easily call the outside serice, without needing to define the model again.
- OpenFeign (usage for Steam API): Check out `eu.lindroos.taas.teams.service.service.external.steam`
    1. Integrate outside service, by manually defining the interface and models.
    2. Easy to mock by implementing the interface locally (you want to e.g. mock locally that something responds in a
       certain way).
- OpenFeign (exposed to other services): `eu.lindroos.taas.teams.service.api`
    1. We define an API in this service, that we expose in a separate package, which can be reused in other services.
    2. Note that we implement and use the same models in our
       RestControllers: `eu.lindroos.taas.teams.service.service.controller`

### Spring Boot (security, controllers, services, etc.)

- Springboot Security with JWT tokens: `eu.lindroos.taas.teams.service.service.security`
    1. This is mainly left-over of original project.
    2. Shows a way use JWT tokens to ID caller.
    3. Uses Springboot security and users to pass the user details to endpoints.
- Springboot Controller: `eu.lindroos.taas.teams.service.service.controller`
    1. It handles the mapping to external models and users services to do business logic.
    2. GenericExceptionHandler (`eu.lindroos.taas.teams.service.service.controller.GenericExceptionHandler`) to handle
       common exceptions thrown by endpoints in the project.
- Services (business logic): `eu.lindroos.taas.teams.service.service.service`
    1. Nothing special, it holds all business logic.

### Lombok

Lombok is a helper library designed for Java, which makes Java much more readable. It simplifies a lot of boilerplate code in Java. You can see these
annotations around data models, response classes, etc.

- Removes the need for getters and setter (commonly replaces by @Data annotation or @Getter / @Setter)
- Easily add constructors by annotation (@NoArgsConstructor, etc.)
- Add class builder with annotation (@Builder)