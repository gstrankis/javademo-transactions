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

## Get started

Prerequisites:

- Java 17
- Docker - to run application locally

To start application

- start the database  
  `docker compose up --detach`
- configure environment variables  
  `EXCHANGERATE_KEY={exchangerate.host api key}`
- run the application  
  `./gradlew bootRun`
