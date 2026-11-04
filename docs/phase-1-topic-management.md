# Phase 1: Topic Management

## What I worked on

In Phase 1, I started building the backend of my AI Study Platform. I implemented the functionality to create, view, update, and delete study topics using Spring Boot and PostgreSQL.

This phase helped me understand how the different layers of a Spring Boot application work together.

## Features implemented

- Create a new study topic
- Get all topics
- Get a topic using its ID
- Update an existing topic
- Delete a topic
- Validate incoming requests
- Handle errors when a topic is not found
- Write unit tests for the service layer

## Backend structure

I followed a layered architecture to keep the code organized.

### TopicController

The controller handles HTTP requests from the client. It receives the request, calls the service, and returns the response.

### TopicService

The service contains the business logic for managing topics. It interacts with the repository and converts entities into response DTOs.

### TopicRepository

I used Spring Data JPA to interact with the PostgreSQL database. It provides methods to save, retrieve, update, and delete topics.

### Topic Entity

The Topic entity represents the topic data stored in the database. It contains fields such as ID, subject, and topic name.

### DTOs

I created `TopicRequest` and `TopicResponse` to separate the data received from the client from the data returned by the API.

`TopicRequest` is used when creating or updating a topic.

`TopicResponse` is used to return topic details to the client.

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/topics` | Create a topic |
| GET | `/api/topics` | Get all topics |
| GET | `/api/topics/{id}` | Get a topic by ID |
| PUT | `/api/topics/{id}` | Update a topic |
| DELETE | `/api/topics/{id}` | Delete a topic |

## Validation and exception handling

I used `@NotBlank` to make sure the subject and topic fields are not empty.

I also implemented a custom `TopicNotFoundException` for cases where a topic does not exist.

The `GlobalExceptionHandler` handles exceptions centrally so that the API can return proper error responses.

## Testing

I wrote unit tests for `TopicService` using JUnit 5 and Mockito.

The tests cover:

- Retrieving topics when the list is empty
- Retrieving multiple topics
- Finding a topic by ID
- Handling a topic that does not exist
- Creating a topic
- Updating a topic
- Deleting a topic

I used Mockito to mock the repository instead of connecting to the actual database during service unit tests.

To run the tests:

```bash
.\mvnw.cmd test
```

## What I learned

During this phase, I learned:

- How REST APIs work in Spring Boot
- Why we separate Controller, Service, and Repository
- How Spring Data JPA interacts with a database
- Why DTOs are useful
- How request validation works
- How to handle exceptions
- How to write unit tests using JUnit and Mockito
- How Mockito verifies repository interactions

## Current status

Phase 1 focuses on the topic management backend. The APIs are implemented, and I am working on verifying the unit tests.

The next phase will focus on user registration, login, and authentication using Spring Security and JWT.
## Status

Completed — all 8 tests passed successfully.
