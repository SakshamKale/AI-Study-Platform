package com.saksham.Aistudyplatform.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void clearAuthenticationBeforeTest() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearAuthenticationAfterTest() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenAuthenticatesUserAndContinuesRequest() throws Exception {
        String email = "student@example.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean[] chainWasCalled = {false};
        FilterChain chain = (req, res) -> chainWasCalled[0] = true;
        UserDetails user = User.withUsername(email)
                .password("encoded-password")
                .roles("USER")
                .build();

        when(jwtService.extractEmail("valid-token")).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(user);

        jwtAuthenticationFilter.doFilter(request, response, chain);

        assertTrue(chainWasCalled[0]);
        assertTrue(SecurityContextHolder.getContext()
                .getAuthentication().isAuthenticated());
        assertEquals(email, SecurityContextHolder.getContext()
                .getAuthentication().getName());
        verify(userDetailsService).loadUserByUsername(email);
    }

    @Test
    void invalidBearerTokenDoesNotAuthenticateUserAndContinuesRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean[] chainWasCalled = {false};
        FilterChain chain = (req, res) -> chainWasCalled[0] = true;

        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        jwtAuthenticationFilter.doFilter(request, response, chain);

        assertTrue(chainWasCalled[0]);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void requestWithoutBearerTokenContinuesWithoutLoadingUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean[] chainWasCalled = {false};
        FilterChain chain = (req, res) -> chainWasCalled[0] = true;

        jwtAuthenticationFilter.doFilter(request, response, chain);

        assertTrue(chainWasCalled[0]);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, userDetailsService);
    }
}
