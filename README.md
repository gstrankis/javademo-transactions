# javademo-transactions

## Purpose

This SpringBoot application demonstrates implementation of service that

- maintains account data, including balance
- processes transactions that transfer funds between accounts
- usees currency exchange rates fetched from external source

## Tech in use

- Spring Data JPA for transactional database access
- Spring RestClient for access of external resources
- Flyway for db migrations
- Gradle as build tool
- Postgres database
- Docker for running database locally

## Run locally

Prerequisites:

- Java 17 installed and JAVA_HOME configured
- Docker - to run application locally

To start application:  
start the database,  
configure environment variables,    
run the application.

```
docker compose up --detach
export EXCHANGERATE_KEY={exchangerate.host api key}
./gradlew bootRun
```

To stop database container:  
`docker compose down`

To stop database container and wipe out all data:  
`docker compose down -v`

To run all tests
`./gradlew cleanTest test`