package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.AuthenticationSessionReader;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InternalAuthenticationControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @MockitoBean private AuthenticationSessionReader authenticationSessionReader;

  @Test
  void context_withAuthenticatedSession_returnsAuthContext() throws Exception {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000101");

    AuthContext authContext = new AuthContext(true, userId, "gribonvald");

    when(authenticationSessionReader.readOidcSession(any())).thenReturn(Optional.of(oidcSession));

    when(authenticationService.getAuthenticatedContext(oidcSession)).thenReturn(authContext);

    mockMvc
        .perform(get("/internal/auth/context"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated").value(true))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.login").value("gribonvald"));

    verify(authenticationSessionReader).readOidcSession(any());
    verify(authenticationService).getAuthenticatedContext(oidcSession);
  }

  @Test
  void context_withoutOidcSession_returnsUnauthorized() throws Exception {
    when(authenticationSessionReader.readOidcSession(any())).thenReturn(Optional.empty());

    mockMvc.perform(get("/internal/auth/context")).andExpect(status().isUnauthorized());

    verify(authenticationSessionReader).readOidcSession(any());
  }

  @Test
  void context_whenAuthenticationServiceThrowsUnauthenticated_returnsUnauthorized()
      throws Exception {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    when(authenticationSessionReader.readOidcSession(any())).thenReturn(Optional.of(oidcSession));

    when(authenticationService.getAuthenticatedContext(oidcSession))
        .thenThrow(new UnauthenticatedSessionException());

    mockMvc.perform(get("/internal/auth/context")).andExpect(status().isUnauthorized());

    verify(authenticationSessionReader).readOidcSession(any());
    verify(authenticationService).getAuthenticatedContext(oidcSession);
  }
}
