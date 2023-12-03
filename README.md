# javademo-transactions

## Purpose

This is SpringBoot application that demonstrates a service that

- maintains account and account history data
- processes transfer funds requests
- fetches currency exchange rates from external source

## Tech in use

- Spring Data JPA for transactional database access
- Spring RestClient for access of external resources
- Flyway for db migrations
- Gradle as build tool
- Postgres database
- Docker for running database locally
- Testcontainers for (almost) integration testing
- Spring MockMvc for testing of Web layer
- Spring RestClient for access of external resources

## Run locally

### Prerequisites:

- Java 17
- Docker

### Run the application:

configure environment variables,    
start the database,  
run the application.

```
export EXCHANGERATE_KEY={exchangerate.host api key}
docker compose up --detach
./gradlew bootRun --args='--spring.profiles.active=local'
```

or create `env` on the basis of `env.sample` and execute  
`./start-local-app.sh`

To stop database container:  
`docker compose down`

To stop database container and wipe out all local data:  
`docker compose down -v`

Run all tests
`./gradlew clean test`

## Swagger UI

http://localhost:8080/swagger-ui/index.html

