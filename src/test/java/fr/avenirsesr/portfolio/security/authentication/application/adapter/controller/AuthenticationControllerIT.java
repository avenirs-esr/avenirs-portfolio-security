package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "avenirs.authentication.oidc.logout.url=https://dev.avenirs-esr.fr/cas/logout",
      "avenirs.authentication.auth.logout.default-service=https://dev.avenirs-esr.fr/cofolio/student",
      "server.servlet.session.cookie.name=AVENIRS_SESSION"
    })
class AuthenticationControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @Test
  void login_redirectsToGeneratedAuthorizationUrl() throws Exception {
    when(authenticationService.generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student"))
        .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");

    mockMvc
        .perform(
            get("/auth/login")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("redirect", "/cofolio/student"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"));

    verify(authenticationService)
        .generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student");
  }

  @Test
  void login_withoutHost_usesLocalhost() throws Exception {
    when(authenticationService.generateAuthorizationUrl("localhost", "/cofolio/student"))
        .thenReturn("https://localhost/cas/oidc/oidcAuthorize");

    mockMvc
        .perform(get("/auth/login").param("redirect", "/cofolio/student"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://localhost/cas/oidc/oidcAuthorize"));

    verify(authenticationService).generateAuthorizationUrl("localhost", "/cofolio/student");
  }

  @Test
  void login_withUnsafeRedirect_usesDefaultRedirect() throws Exception {
    when(authenticationService.generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student"))
        .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");

    mockMvc
        .perform(
            get("/auth/login")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("redirect", "https://evil.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"));

    verify(authenticationService)
        .generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student");
  }

  @Test
  void callback_withoutCode_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(
            get("/auth/callback")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("state", "/cofolio/student"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void callback_withBlankCode_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(
            get("/auth/callback")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("code", " ")
                .param("state", "/cofolio/student"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void callback_withCode_storesOidcSessionAndRedirectsToState() throws Exception {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    when(authenticationService.createSessionFromAuthorizationCode("dev.avenirs-esr.fr", "code"))
        .thenReturn(oidcSession);

    mockMvc
        .perform(
            get("/auth/callback")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("code", "code")
                .param("state", "/cofolio/student"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
        .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession));

    verify(authenticationService).createSessionFromAuthorizationCode("dev.avenirs-esr.fr", "code");
  }

  @Test
  void callback_withUnsafeState_redirectsToDefaultPath() throws Exception {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    when(authenticationService.createSessionFromAuthorizationCode("dev.avenirs-esr.fr", "code"))
        .thenReturn(oidcSession);

    mockMvc
        .perform(
            get("/auth/callback")
                .header("x-forwarded-host", "dev.avenirs-esr.fr")
                .param("code", "code")
                .param("state", "https://evil.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
        .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession));
  }

  @Test
  void logout_invalidatesSession_clearsCookieAndRedirectsToCasLogout() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(
        SessionAttributes.OIDC_SESSION,
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z")));

    mockMvc
        .perform(get("/auth/logout").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header()
                .string(
                    "Location",
                    "https://dev.avenirs-esr.fr/cas/logout?service=https://dev.avenirs-esr.fr/cofolio/student"))
        .andExpect(cookie().maxAge("AVENIRS_SESSION", 0))
        .andExpect(cookie().httpOnly("AVENIRS_SESSION", true))
        .andExpect(cookie().secure("AVENIRS_SESSION", true))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));
  }

  @Test
  void logout_withoutExistingSession_clearsCookieAndRedirectsToCasLogout() throws Exception {
    mockMvc
        .perform(get("/auth/logout"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header()
                .string(
                    "Location",
                    "https://dev.avenirs-esr.fr/cas/logout?service=https://dev.avenirs-esr.fr/cofolio/student"))
        .andExpect(cookie().maxAge("AVENIRS_SESSION", 0));
  }
}
