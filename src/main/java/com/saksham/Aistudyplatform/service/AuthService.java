
package com.saksham.Aistudyplatform.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saksham.Aistudyplatform.dto.AuthResponse;
import com.saksham.Aistudyplatform.entity.AppUser;
import com.saksham.Aistudyplatform.exception.EmailAlreadyRegisteredException;
import com.saksham.Aistudyplatform.exception.InvalidCredentialsException;
import com.saksham.Aistudyplatform.repository.AppUserRepository;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AppUser register(String name, String email, String password) {

        if (appUserRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        String encodedPassword = passwordEncoder.encode(password);

        AppUser user = new AppUser(name, email, encodedPassword);

        return appUserRepository.save(user);
    }

    public AuthResponse login(String email, String password) {

        Optional<AppUser> optionalUser =
                appUserRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        AppUser user = optionalUser.get();

        boolean passwordMatches = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName()
        );
    }
}
