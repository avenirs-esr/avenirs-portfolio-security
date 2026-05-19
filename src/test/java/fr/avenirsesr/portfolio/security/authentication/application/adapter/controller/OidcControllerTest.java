package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration.SpringSecurityConfig;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OidcController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SpringSecurityConfig.class)
@TestPropertySource(
    properties = {
      "springdoc.api-docs.path=/api-docs",
      "springdoc.swagger-ui.path=/swagger-ui",
      "avenirs.authentication.oidc.callback=/oidc/callback",
      "avenirs.authentication.oidc.callback.redirect=/oidc/callback/redirect",
      "avenirs.authentication.oidc.callback.profile=/oidc/callback/profile",
      "avenirs.authentication.oidc.callback.introspect=/oidc/callback/introspect",
      "management.actuator.health.path=/actuator/health"
    })
class OidcControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private BaseUserService baseUserService;

  @Nested
  class GivenOidcController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an OIDC controller");
    }

    @Nested
    class WhenLoginIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("login is requested");
      }

      @Nested
      class AndCredentialsAreValid {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("credentials are valid");

          when(oidcService.getAccessToken(eq("user"), eq("pass")))
              .thenReturn(Optional.of(accessToken()));
        }

        @Test
        void thenItShouldReturnAccessToken() throws Exception {
          BddLogger.then("it should return access token");

          mockMvc
              .perform(
                  post("/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{\"login\":\"user\",\"password\":\"pass\"}"))
              .andExpect(status().isOk())
              .andExpect(content().string("AT"));

          verify(oidcService).getAccessToken("user", "pass");
        }
      }

      @Nested
      class AndCredentialsAreInvalid {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("credentials are invalid");

          when(oidcService.getAccessToken(any(), any())).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc
              .perform(
                  post("/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{\"login\":\"user\",\"password\":\"bad\"}"))
              .andExpect(status().isUnauthorized());
        }
      }
    }

    @Nested
    class WhenOidcCallbackIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("OIDC callback is requested");
      }

      @Nested
      class AndHostAndCodeAreProvided {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host and code are provided");

          when(oidcService.exchangeAuthorizationCodeForToken("test-host.com", "code"))
              .thenReturn(accessToken());
        }

        @Test
        void thenItShouldMapDomainToPayload() throws Exception {
          BddLogger.then("it should map domain to payload");

          mockMvc
              .perform(
                  get("/oidc/callback")
                      .header("x-forwarded-host", "test-host.com")
                      .param("code", "code"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.access_token").value("AT"));

          verify(oidcService).exchangeAuthorizationCodeForToken("test-host.com", "code");
        }
      }

      @Nested
      class AndHostAndCodeAreMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host and code are missing");

          when(oidcService.exchangeAuthorizationCodeForToken("localhost", null))
              .thenReturn(accessToken());
        }

        @Test
        void thenItShouldUseLocalhostAndNullCode() throws Exception {
          BddLogger.then("it should use localhost and null code");

          mockMvc
              .perform(get("/oidc/callback"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.access_token").value("AT"));

          verify(oidcService).exchangeAuthorizationCodeForToken("localhost", null);
        }
      }
    }

    @Nested
    class WhenRedirectIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("redirect is requested");
      }

      @Nested
      class AndHostIsProvided {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host is provided");

          when(oidcService.generateServiceURL("test-host.com"))
              .thenReturn("https://test-host.com/target");
        }

        @Test
        void thenItShouldReturn302WithLocation() throws Exception {
          BddLogger.then("it should return 302 with location");

          mockMvc
              .perform(get("/oidc/callback/redirect").header("x-forwarded-host", "test-host.com"))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://test-host.com/target"));

          verify(oidcService).generateServiceURL("test-host.com");
        }
      }

      @Nested
      class AndHostIsMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host is missing");

          when(oidcService.generateServiceURL("localhost")).thenReturn("https://localhost/target");
        }

        @Test
        void thenItShouldUseLocalhost() throws Exception {
          BddLogger.then("it should use localhost");

          mockMvc
              .perform(get("/oidc/callback/redirect"))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://localhost/target"));

          verify(oidcService).generateServiceURL("localhost");
        }
      }
    }

    @Nested
    class WhenProfileIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("profile is requested");
      }

      @Nested
      class AndTokenIsActive {
        private String token;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is active");

          token = "AT";

          when(oidcService.introspectAccessToken(token))
              .thenReturn(new OIDCIntrospection(token, true, "usn", null));
          when(oidcService.profile(token))
              .thenReturn(new OIDCProfile("id", "svc", "fn", "ln", "mail"));
        }

        @Test
        void thenItShouldReturnProfile() throws Exception {
          BddLogger.then("it should return profile");

          mockMvc
              .perform(post("/oidc/callback/profile").header("x-authorization", token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value("id"))
              .andExpect(jsonPath("$.service").value("svc"))
              .andExpect(jsonPath("$.firstName").value("fn"))
              .andExpect(jsonPath("$.lastName").value("ln"))
              .andExpect(jsonPath("$.email").value("mail"));

          verify(oidcService).introspectAccessToken(token);
          verify(oidcService).profile(token);
        }
      }

      @Nested
      class AndTokenIsInactive {
        private String token;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is inactive");

          token = "inactive";

          when(oidcService.introspectAccessToken(token))
              .thenReturn(new OIDCIntrospection(token, false, null, null));
        }

        @Test
        void thenItShouldReturnForbidden() throws Exception {
          BddLogger.then("it should return forbidden");

          mockMvc
              .perform(post("/oidc/callback/profile").header("x-authorization", token))
              .andExpect(status().isForbidden());

          verify(oidcService).introspectAccessToken(token);
        }
      }
    }

    @Nested
    class WhenIntrospectionIsRequested {
      private String token;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("introspection is requested");

        token = "AT";

        when(oidcService.introspectAccessToken(token))
            .thenReturn(
                new OIDCIntrospection(
                    token, true, "usn", UUID.fromString("00000000-0000-0000-0000-000000000101")));
      }

      @Test
      void thenItShouldReturnIntrospectionPayload() throws Exception {
        BddLogger.then("it should return introspection payload");

        mockMvc
            .perform(post("/oidc/callback/introspect").header("x-authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.userId").value("00000000-0000-0000-0000-000000000101"))
            .andExpect(jsonPath("$.uniqueSecurityName").value("usn"));

        verify(oidcService).introspectAccessToken(token);
      }
    }
  }

  private OIDCAccessToken accessToken() {
    return new OIDCAccessToken("AT", "AT", "Bearer", 3600, "openid", null, null, false);
  }
}
