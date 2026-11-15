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

`TopicServiceTest` uses JUnit 5 and Mockito. The current tests set up an authenticated user and mock both repositories because topic operations are scoped to the current owner.

The tests cover:

- Listing topics using the authenticated user's email
- Finding a topic by ID and owner
- Returning not found for a missing or non-owned topic
- Assigning the authenticated user as topic owner during creation
- Updating and deleting owned topics
- Rejecting update and delete attempts for a topic not owned by the current user

I used Mockito to mock the repository instead of connecting to the actual database during service unit tests.

The latest full test suite passed: 21 tests, with no failures or errors. `TopicServiceTest` contributes nine tests. The earlier note that all eight tests passed referred to an older version of the test file.

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

The topic CRUD implementation is present. The current service tests pass and cover authenticated, owner-scoped behavior. The two-user API flow is also covered by `AuthFlowIntegrationTest` using a rollback-only database transaction.

Authentication and user-owned topic access are documented in [PHASE-2-AUTHENTICATION.md](PHASE-2-AUTHENTICATION.md).
