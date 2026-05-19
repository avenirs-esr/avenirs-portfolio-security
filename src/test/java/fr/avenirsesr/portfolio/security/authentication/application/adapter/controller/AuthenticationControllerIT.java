package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  private static final String HOST = "dev.avenirs-esr.fr";
  private static final String REDIRECT = "/cofolio/student";
  private static final String CODE = "code";
  private static final String PKCE_CODE_VERIFIER = "pkce-code-verifier";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @Nested
  class GivenAuthenticationController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an authentication controller");
    }

    @Nested
    class WhenLoginIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("login is requested");
      }

      @Nested
      class AndHostAndSafeRedirectAreProvided {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host and safe redirect are provided");

          when(authenticationService.generateAuthorizationUrl(eq(HOST), eq(REDIRECT), anyString()))
              .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldRedirectToGeneratedAuthorizationUrl() throws Exception {
          BddLogger.then("it should redirect to generated authorization URL");

          mockMvc
              .perform(
                  get("/auth/login").header("x-forwarded-host", HOST).param("redirect", REDIRECT))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"))
              .andExpect(request().sessionAttributeDoesNotExist(SessionAttributes.OIDC_SESSION));

          verify(authenticationService)
              .generateAuthorizationUrl(eq(HOST), eq(REDIRECT), anyString());
        }
      }

      @Nested
      class AndHostIsMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host is missing");

          when(authenticationService.generateAuthorizationUrl(eq(null), eq(REDIRECT), anyString()))
              .thenReturn("https://localhost/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldDelegateNullHostAndRedirect() throws Exception {
          BddLogger.then("it should delegate null host and redirect");

          mockMvc
              .perform(get("/auth/login").param("redirect", REDIRECT))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://localhost/cas/oidc/oidcAuthorize"));

          verify(authenticationService)
              .generateAuthorizationUrl(eq(null), eq(REDIRECT), anyString());
        }
      }

      @Nested
      class AndRedirectIsUnsafe {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("redirect is unsafe");

          when(authenticationService.generateAuthorizationUrl(eq(HOST), eq(REDIRECT), anyString()))
              .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldUseDefaultRedirect() throws Exception {
          BddLogger.then("it should use default redirect");

          mockMvc
              .perform(
                  get("/auth/login")
                      .header("x-forwarded-host", HOST)
                      .param("redirect", "https://evil.com"))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"));

          verify(authenticationService)
              .generateAuthorizationUrl(eq(HOST), eq(REDIRECT), anyString());
        }
      }
    }

    @Nested
    class WhenCallbackIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("callback is requested");
      }

      @Test
      void thenItShouldReturnUnauthorizedWhenCodeIsMissing() throws Exception {
        mockMvc
            .perform(
                get("/auth/callback").header("x-forwarded-host", HOST).param("state", REDIRECT))
            .andExpect(status().isUnauthorized());
      }

      @Test
      void thenItShouldReturnUnauthorizedWhenCodeIsBlank() throws Exception {
        mockMvc
            .perform(
                get("/auth/callback")
                    .header("x-forwarded-host", HOST)
                    .param("code", " ")
                    .param("state", REDIRECT))
            .andExpect(status().isUnauthorized());
      }

      @Test
      void thenItShouldReturnUnauthorizedWhenPkceVerifierIsMissing() throws Exception {
        mockMvc
            .perform(
                get("/auth/callback")
                    .header("x-forwarded-host", HOST)
                    .param("code", CODE)
                    .param("state", REDIRECT))
            .andExpect(status().isUnauthorized());
      }

      @Test
      void thenItShouldStoreOidcSessionAndRedirectToState() throws Exception {
        OIDCSession oidcSession = oidcSession();
        MockHttpSession session = sessionWithPkceVerifier();

        when(authenticationService.createSessionFromAuthorizationCode(
                HOST, CODE, PKCE_CODE_VERIFIER))
            .thenReturn(oidcSession);

        mockMvc
            .perform(
                get("/auth/callback")
                    .session(session)
                    .header("x-forwarded-host", HOST)
                    .param("code", CODE)
                    .param("state", REDIRECT))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
            .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession))
            .andExpect(
                request().sessionAttributeDoesNotExist(SessionAttributes.PKCE_CODE_VERIFIER));

        verify(authenticationService)
            .createSessionFromAuthorizationCode(HOST, CODE, PKCE_CODE_VERIFIER);
      }

      @Test
      void thenItShouldRedirectToDefaultPathWhenStateIsUnsafe() throws Exception {
        OIDCSession oidcSession = oidcSession();
        MockHttpSession session = sessionWithPkceVerifier();

        when(authenticationService.createSessionFromAuthorizationCode(
                HOST, CODE, PKCE_CODE_VERIFIER))
            .thenReturn(oidcSession);

        mockMvc
            .perform(
                get("/auth/callback")
                    .session(session)
                    .header("x-forwarded-host", HOST)
                    .param("code", CODE)
                    .param("state", "https://evil.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
            .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession))
            .andExpect(
                request().sessionAttributeDoesNotExist(SessionAttributes.PKCE_CODE_VERIFIER));

        verify(authenticationService)
            .createSessionFromAuthorizationCode(HOST, CODE, PKCE_CODE_VERIFIER);
      }
    }

    @Nested
    class WhenLogoutIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("logout is requested");
      }

      @Test
      void thenItShouldInvalidateSessionClearCookieAndRedirectToCasLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionAttributes.OIDC_SESSION, oidcSession());

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
      void thenItShouldClearCookieAndRedirectToCasLogoutWhenSessionDoesNotExist() throws Exception {
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
  }

  private MockHttpSession sessionWithPkceVerifier() {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionAttributes.PKCE_CODE_VERIFIER, PKCE_CODE_VERIFIER);
    return session;
  }

  private OIDCSession oidcSession() {
    return new OIDCSession(
        "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));
  }
}
