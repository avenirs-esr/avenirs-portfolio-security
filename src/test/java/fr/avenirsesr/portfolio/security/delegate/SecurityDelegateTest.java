package fr.avenirsesr.portfolio.security.delegate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityDelegateTest {

    private AutoCloseable closeable;

    private SecurityDelegate securityDelegate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {

        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        securityDelegate = new SecurityDelegate();
    }


    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void getAuthenticatedUserLoginSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user123");

        String result = securityDelegate.getAuthenticatedUserLogin();

        assertEquals("user123", result, "Login verification");
    }

    @Test
    void testGetAuthenticatedUserLoginUnauthenticatedForbidden() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> securityDelegate.getAuthenticatedUserLogin(),
                HttpStatus.FORBIDDEN.getReasonPhrase());
    }

    @Test
    void testGetAuthenticatedUserLoginForbiddenNoAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(ResponseStatusException.class,
                () -> securityDelegate.getAuthenticatedUserLogin(),
                HttpStatus.FORBIDDEN.getReasonPhrase());
    }
}