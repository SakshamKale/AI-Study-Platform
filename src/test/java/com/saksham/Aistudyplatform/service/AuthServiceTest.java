package com.saksham.Aistudyplatform.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saksham.Aistudyplatform.dto.AuthResponse;
import com.saksham.Aistudyplatform.entity.AppUser;
import com.saksham.Aistudyplatform.exception.EmailAlreadyRegisteredException;
import com.saksham.Aistudyplatform.exception.InvalidCredentialsException;
import com.saksham.Aistudyplatform.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerEncodesPasswordBeforeSavingUser() {
        when(appUserRepository.existsByEmail("student@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("plain-password"))
                .thenReturn("encoded-password");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = authService.register(
                "Student", "student@example.com", "plain-password");

        assertEquals("Student", result.getName());
        assertEquals("student@example.com", result.getEmail());
        assertEquals("encoded-password", result.getPassword());

        ArgumentCaptor<AppUser> savedUser = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(savedUser.capture());
        assertEquals("encoded-password", savedUser.getValue().getPassword());
    }

    @Test
    void registerRejectsEmailThatIsAlreadyUsed() {
        when(appUserRepository.existsByEmail("student@example.com"))
                .thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> authService.register(
                        "Student", "student@example.com", "plain-password"));

        verify(passwordEncoder, never()).encode(anyString());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void loginReturnsTokenWhenCredentialsAreCorrect() {
        AppUser user = new AppUser(
                "Student", "student@example.com", "encoded-password");
        when(appUserRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-password"))
                .thenReturn(true);
        when(jwtService.generateToken("student@example.com"))
                .thenReturn("test-jwt");

        AuthResponse response = authService.login(
                "student@example.com", "plain-password");

        assertEquals("test-jwt", response.getToken());
        assertEquals("student@example.com", response.getEmail());
        assertEquals("Student", response.getName());
    }

    @Test
    void loginRejectsEmailThatDoesNotExist() {
        when(appUserRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login("unknown@example.com", "plain-password"));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginRejectsIncorrectPassword() {
        AppUser user = new AppUser(
                "Student", "student@example.com", "encoded-password");
        when(appUserRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login("student@example.com", "wrong-password"));

        verify(jwtService, never()).generateToken(anyString());
    }
}
