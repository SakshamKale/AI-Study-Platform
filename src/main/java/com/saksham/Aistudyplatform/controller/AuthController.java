
package com.saksham.Aistudyplatform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.Aistudyplatform.dto.AuthResponse;
import com.saksham.Aistudyplatform.dto.LoginRequest;
import com.saksham.Aistudyplatform.dto.RegisterRequest;
import com.saksham.Aistudyplatform.entity.AppUser;
import com.saksham.Aistudyplatform.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser register(@Valid @RequestBody RegisterRequest request) {

        return authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );
    }
    
@PostMapping("/login")
public AuthResponse login(@Valid @RequestBody LoginRequest request) {

    return authService.login(
            request.getEmail(),
            request.getPassword()
    );
}

}
