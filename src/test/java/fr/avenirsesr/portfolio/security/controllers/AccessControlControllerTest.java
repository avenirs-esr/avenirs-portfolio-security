package fr.avenirsesr.portfolio.security.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.models.*;
import fr.avenirsesr.portfolio.security.services.AccessControlService;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.transaction.Transactional;


/**
 * <h1>AccessControlControllerTest</h1>
 * <p>
 * Description: general test case, bad header, missing token, etc. See specific tests are located in specific test files.
 *
 * <h2>Version:</h2>
 * 1.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 18 Sept 2024
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class AccessControlControllerTest {

    @Value("${avenirs.access.control}")
    private String accessControlEndPoint;

    @Value("${avenirs.access.control.feedback}")
    private String feedbackEndPoint;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AccessControlService accessControlService;

    @InjectMocks
    private AccessControlController accessControlController;

    private AutoCloseable closeable;

    private AccessControlGrantRequest grantRequest;

    private AccessControlRevokeRequest revokeRequest;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        grantRequest = new AccessControlGrantRequest()
                .setRoleId(1L)
                .setResourceIds(new Long[]{1L, 2L})
                .setValidityStart("2024-10-01")
                .setValidityEnd("2024-12-31")
                .setStructureIds(new Long[]{1L, 2L});

        revokeRequest = new AccessControlRevokeRequest()
                .setUid("user123")
                .setRoleId(1L)
                .setScopeId(1L)
                .setContextId(1L);
    }
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void grantAccessSuccess() {

        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(true);
        introspectResponse.setUniqueSecurityName("user123");

        when(authenticationService.introspectAccessToken("valid-token")).thenReturn(introspectResponse);

        AccessControlGrantResponse grantResponse = new AccessControlGrantResponse()
                .setLogin("user123")
                .setGranted(true);

        when(accessControlService.grantAccess(any(AccessControlGrantRequest.class))).thenReturn(grantResponse);

        ResponseEntity<AccessControlGrantResponse> response = accessControlController.grantAccess("valid-token", grantRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body not null");
        assertTrue(response.getBody().isGranted());
        assertEquals("user123", response.getBody().getLogin());
    }

    @Test
    void grantAccessWithToInactiveToken() {
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(false);

        when(authenticationService.introspectAccessToken("inactive-token")).thenReturn(introspectResponse);

        ResponseEntity<AccessControlGrantResponse> response = accessControlController.grantAccess("inactive-token", grantRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void grantAccessWithServiceError() {
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(true);
        introspectResponse.setUniqueSecurityName("user123");

        when(authenticationService.introspectAccessToken("valid-token")).thenReturn(introspectResponse);

        when(accessControlService.grantAccess(any(AccessControlGrantRequest.class))).thenThrow(new RuntimeException("Error during access granting"));

        ResponseEntity<AccessControlGrantResponse> response = accessControlController.grantAccess("valid-token", grantRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user123", response.getBody().getLogin());
        assertFalse(response.getBody().isGranted());
        assertEquals("Error during access granting", response.getBody().getError());
    }

    @Test
    void revokeAccessSuccess() {
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(true);
        introspectResponse.setUniqueSecurityName("user123");

        when(authenticationService.introspectAccessToken("valid-token")).thenReturn(introspectResponse);

        AccessControlRevokeResponse revokeResponse = new AccessControlRevokeResponse()
                .setLogin("user123")
                .setRevoked(true);

        when(accessControlService.revokeAccess(any(AccessControlRevokeRequest.class))).thenReturn(revokeResponse);

        ResponseEntity<AccessControlRevokeResponse> response = accessControlController.revokeAccess("valid-token", revokeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "response body not null");
        assertTrue(response.getBody().isRevoked());
        assertEquals("user123", response.getBody().getLogin());
    }

    @Test
    void revokeAccessWithInactiveToken() {
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(false);

        when(authenticationService.introspectAccessToken("inactive-token")).thenReturn(introspectResponse);

        ResponseEntity<AccessControlRevokeResponse> response = accessControlController.revokeAccess("inactive-token", revokeRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void revokeAccessWithServiceError() {
        OIDCIntrospectResponse introspectResponse = new OIDCIntrospectResponse();
        introspectResponse.setActive(true);
        introspectResponse.setUniqueSecurityName("user123");

        when(authenticationService.introspectAccessToken("valid-token")).thenReturn(introspectResponse);

        when(accessControlService.revokeAccess(any(AccessControlRevokeRequest.class))).thenThrow(new RuntimeException("Error during access revoking"));

        ResponseEntity<AccessControlRevokeResponse> response = accessControlController.revokeAccess("valid-token", revokeRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user123", response.getBody().getLogin());
        assertFalse(response.getBody().isRevoked());
        assertEquals("Error during access revoking", response.getBody().getError());
    }


    @Test
    void testIsAuthorizedWithoutHeader() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("uri", "/ac")
                        .param("method", HttpMethod.GET.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void testIsAuthorizedWithoutURI() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("method", HttpMethod.GET.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIsAuthorizedWithoutMethod() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("uri", feedbackEndPoint))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void testIsAuthorizedNotGranted() throws Exception {

        String token = "invalid token";
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", token)
                        .param("uri", feedbackEndPoint)
                        .param("method", HttpMethod.GET.name())
                        .param("resource", "1"))
                .andDo(print()).andExpect(status().isForbidden());


    }


}
