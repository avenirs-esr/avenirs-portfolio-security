package fr.avenirsesr.portfolio.security.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.delegate.SecurityDelegate;
import fr.avenirsesr.portfolio.security.model.*;
import fr.avenirsesr.portfolio.security.service.AccessControlService;
import fr.avenirsesr.portfolio.security.service.AuthenticationService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.UUID;


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

    @Autowired
    private SecurityDelegate securityDelegate;

    @InjectMocks
    private AccessControlController accessControlController;

    private AutoCloseable closeable;

    private AccessControlGrantRequest grantRequest;

    private AccessControlRevokeRequest revokeRequest;


    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(accessControlController, "securityDelegate", securityDelegate);

        closeable = MockitoAnnotations.openMocks(this);

        grantRequest = new AccessControlGrantRequest()
                .setRoleId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setResourceIds(new UUID[]{
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                })
                .setValidityStart("2024-10-01")
                .setValidityEnd("2024-12-31")
                .setStructureIds(new UUID[]{
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                });

        revokeRequest = new AccessControlRevokeRequest()
                .setLogin("user123")
                .setRoleId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setScopeId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setContextId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void grantAccessSuccess() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", "valid-token", new ArrayList<>())
        );

        AccessControlGrantResponse grantResponse = new AccessControlGrantResponse()
                .setLogin("user123")
                .setGranted(true);

        when(accessControlService.grantAccess(any(AccessControlGrantRequest.class))).thenReturn(grantResponse);

        ResponseEntity<AccessControlGrantResponse> response = accessControlController.grantAccess(grantRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body not null");
        assertTrue(response.getBody().isGranted());
        assertEquals("user123", response.getBody().getLogin());
    }

    @Test
    void grantAccessWithServiceError() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", "valid-token", new ArrayList<>())
        );

        when(accessControlService.grantAccess(any(AccessControlGrantRequest.class))).thenThrow(new RuntimeException("Error during access granting"));

        ResponseEntity<AccessControlGrantResponse> response = accessControlController.grantAccess(grantRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user123", response.getBody().getLogin());
        assertFalse(response.getBody().isGranted());
        assertEquals("Error during access granting", response.getBody().getError());
    }

    @Test
    void revokeAccessSuccess() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", "valid-token", new ArrayList<>())
        );

        AccessControlRevokeResponse revokeResponse = new AccessControlRevokeResponse()
                .setLogin("user123")
                .setRevoked(true);

        when(accessControlService.revokeAccess(any(AccessControlRevokeRequest.class))).thenReturn(revokeResponse);

        ResponseEntity<AccessControlRevokeResponse> response = accessControlController.revokeAccess(revokeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "response body not null");
        assertTrue(response.getBody().isRevoked());
        assertEquals("user123", response.getBody().getLogin());
    }

    @Test
    void revokeAccessWithServiceError() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", "valid-token", new ArrayList<>())
        );

        when(accessControlService.revokeAccess(any(AccessControlRevokeRequest.class))).thenThrow(new RuntimeException("Error during access revoking"));

        ResponseEntity<AccessControlRevokeResponse> response = accessControlController.revokeAccess(revokeRequest);

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
                .andExpect(status().isForbidden());

    }

    @Test
    void testIsAuthorizedWithoutURI() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("method", HttpMethod.GET.name()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testIsAuthorizedWithoutMethod() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("uri", feedbackEndPoint))
                .andDo(print()).andExpect(status().isForbidden());
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
