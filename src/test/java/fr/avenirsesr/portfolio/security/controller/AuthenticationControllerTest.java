package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.model.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.model.OIDCProfileResponse;
import fr.avenirsesr.portfolio.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {


    @Value("${avenirs.test.authentication.controller.user.login}")
    private String userLogin;

    @Value("${avenirs.test.authentication.controller.user.password}")
    private String userPassword;

    @Value("${avenirs.test.authentication.controller.profile.expected.id}")
    private String profileExpectedId;

    @Value("${avenirs.test.authentication.controller.profile.expected.service}")
    private String profileExpectedService;

    @Value("${avenirs.test.authentication.controller.profile.expected.first.name}")
    private String profileExpectedFirstName;

    @Value("${avenirs.test.authentication.controller.profile.expected.last.name}")
    private String profileExpectedLastName;

    @Value("${avenirs.test.authentication.controller.profile.expected.email}")
    private String profileExpectedEmail;


    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AutoCloseable closeable;

    @Autowired
    AccessTokenHelper accessTokenHelper;

    @Autowired
    private AuthenticationController notMockedAuthenticationController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void oidcCallbackWithHostAndCode() {
        String forwardHost = "test-host.com";
        String code = "testCode";
        String expectedUrl = this.authenticationService.generateAuthorizeURL(forwardHost, code);

        when(authenticationService.generateAuthorizeURL(forwardHost, code)).thenReturn(expectedUrl);

        try {
            authenticationController.oidcCallback(forwardHost, response, code);
            verify(response).sendRedirect(expectedUrl);
        } catch (IOException e) {
            fail("IOException should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void oidcCallbackWithoutHostAndCode() {
        String expectedUrl = this.authenticationService.generateAuthorizeURL("localhost", AuthenticationController.NO_PROVIDED_CODE);
        when(authenticationService.generateAuthorizeURL(null, null)).thenReturn(expectedUrl);

        try {
            authenticationController.oidcCallback(null, response, null);
            verify(response).sendRedirect(expectedUrl);
        } catch (IOException e) {
            fail("IOException should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void profileWithValidToken() {
        try {
            String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
            OIDCProfileResponse response = notMockedAuthenticationController.profile(token);
            assertNotNull(response, "Profile response not nul vor valid token");
            assertEquals(profileExpectedId, response.getId(), "Profile response id");
            assertEquals(profileExpectedService, response.getService(), "Profile response service");
            assertEquals(profileExpectedFirstName, response.getFirstName(), "Profile response first name");
            assertEquals(profileExpectedLastName, response.getLastName(), "Profile response last name");
            assertEquals(profileExpectedEmail, response.getEmail(), "Profile response email");

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void profileWithInactiveToken() {
        String token = "inactive-token";
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(false);

        when(authenticationService.introspectAccessToken(token)).thenReturn(introspectResponse);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authenticationController.profile(token));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(authenticationService).introspectAccessToken(token);
        verify(authenticationService, never()).profile(token);
    }

    @Test
    void profileWithNullIntrospectResponse() {
        String token = "invalid-token";

        when(authenticationService.introspectAccessToken(token)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authenticationController.profile(token));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(authenticationService).introspectAccessToken(token);
        verify(authenticationService, never()).profile(token);
    }

    @Test
    void redirectWithHost() throws IOException {
        String host = "test-host.com";
        String expectedUrl = this.authenticationService.generateServiceURL(host);

        when(authenticationService.generateServiceURL(host)).thenReturn(expectedUrl);
        authenticationController.redirect(host, response);
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void redirectWithoutHost() throws IOException {
        String expectedUrl = this.authenticationService.generateServiceURL("localhost");

        when(authenticationService.generateServiceURL(null)).thenReturn(expectedUrl);
        authenticationController.redirect(null, response);
        verify(response).sendRedirect(expectedUrl);
    }


    @Test
    void introspectWithValidToken() {

        try {
            String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
            OIDCIntrospectResponse response = notMockedAuthenticationController.introspect(token);
            assertNotNull(response, "Introspect response not nul vor valid token");
            assertEquals(token, response.getToken(), "Introspect response token");
            assertEquals(userLogin, response.getUniqueSecurityName(), "Introspect response uniqueSecurityName");
            assertTrue(response.isActive(), "Introspect response active");

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void introspectWithInvalidToken() {

        try {
            String token = "invalid-token";
            OIDCIntrospectResponse response = notMockedAuthenticationController.introspect(token);
            assertNotNull(response, "Introspect response not nul vor valid token");
            assertNull(response.getUniqueSecurityName(),"Introspect null security name");
            assertFalse(response.isActive(),"Introspect not active");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

    }
}