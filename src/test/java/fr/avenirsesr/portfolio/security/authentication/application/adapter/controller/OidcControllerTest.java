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

import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration.SpringSecurityConfig;
import java.util.Optional;
import java.util.UUID;
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

  @Test
  void login_returnsAccessToken() throws Exception {
    when(oidcService.getAccessToken(eq("user"), eq("pass")))
        .thenReturn(
            Optional.of(
                new OIDCAccessToken("AT", "AT", "Bearer", 3600, "openid", null, null, false)));

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"user\",\"password\":\"pass\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("AT"));

    verify(oidcService).getAccessToken("user", "pass");
  }

  @Test
  void login_invalidCredentials_returns401() throws Exception {
    when(oidcService.getAccessToken(any(), any())).thenReturn(Optional.empty());

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"user\",\"password\":\"bad\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void oidcCallback_mapsDomainToPayload() throws Exception {
    when(oidcService.exchangeAuthorizationCodeForToken("test-host.com", "code"))
        .thenReturn(new OIDCAccessToken("AT", "AT", "Bearer", 3600, "openid", null, null, false));

    mockMvc
        .perform(
            get("/oidc/callback").header("x-forwarded-host", "test-host.com").param("code", "code"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").value("AT"));

    verify(oidcService).exchangeAuthorizationCodeForToken("test-host.com", "code");
  }

  @Test
  void oidcCallback_withoutHostAndCode_usesLocalhostAndNullCode() throws Exception {
    when(oidcService.exchangeAuthorizationCodeForToken("localhost", null))
        .thenReturn(new OIDCAccessToken("AT", "AT", "Bearer", 3600, "openid", null, null, false));

    mockMvc
        .perform(get("/oidc/callback"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").value("AT"));

    verify(oidcService).exchangeAuthorizationCodeForToken("localhost", null);
  }

  @Test
  void redirect_returns302WithLocation() throws Exception {
    when(oidcService.generateServiceURL("test-host.com"))
        .thenReturn("https://test-host.com/target");

    mockMvc
        .perform(get("/oidc/callback/redirect").header("x-forwarded-host", "test-host.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://test-host.com/target"));

    verify(oidcService).generateServiceURL("test-host.com");
  }

  @Test
  void redirect_withoutHost_usesLocalhost() throws Exception {
    when(oidcService.generateServiceURL("localhost")).thenReturn("https://localhost/target");

    mockMvc
        .perform(get("/oidc/callback/redirect"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://localhost/target"));

    verify(oidcService).generateServiceURL("localhost");
  }

  @Test
  void profile_activeToken_returnsProfile() throws Exception {
    String token = "AT";

    when(oidcService.introspectAccessToken(token))
        .thenReturn(new OIDCIntrospection(token, true, "usn", null));
    when(oidcService.profile(token)).thenReturn(new OIDCProfile("id", "svc", "fn", "ln", "mail"));

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

  @Test
  void profile_inactiveToken_returns403() throws Exception {
    String token = "inactive";

    when(oidcService.introspectAccessToken(token))
        .thenReturn(new OIDCIntrospection(token, false, null, null));

    mockMvc
        .perform(post("/oidc/callback/profile").header("x-authorization", token))
        .andExpect(status().isForbidden());

    verify(oidcService).introspectAccessToken(token);
  }

  @Test
  void introspect_returnsIntrospectionPayload() throws Exception {
    String token = "AT";

    when(oidcService.introspectAccessToken(token))
        .thenReturn(
            new OIDCIntrospection(
                token, true, "usn", UUID.fromString("00000000-0000-0000-0000-000000000101")));

    mockMvc
        .perform(post("/oidc/callback/introspect").header("x-authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.userId").value("00000000-0000-0000-0000-000000000101"))
        .andExpect(jsonPath("$.uniqueSecurityName").value("usn"));

    verify(oidcService).introspectAccessToken(token);
  }
}
