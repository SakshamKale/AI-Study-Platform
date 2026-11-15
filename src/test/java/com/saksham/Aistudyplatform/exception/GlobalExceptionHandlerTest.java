package com.saksham.Aistudyplatform.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler =
            new GlobalExceptionHandler();

    @Test
    void duplicateEmailMapsToConflictResponse() {
        ResponseEntity<String> response = exceptionHandler
                .handleEmailAlreadyRegistered(new EmailAlreadyRegisteredException());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already registered", response.getBody());
    }

    @Test
    void invalidCredentialsMapToUnauthorizedResponse() {
        ResponseEntity<String> response = exceptionHandler
                .handleInvalidCredentials(new InvalidCredentialsException());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }
}
