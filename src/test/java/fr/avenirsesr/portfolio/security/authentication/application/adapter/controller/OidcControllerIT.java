package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@TestPropertySource(
    properties = {
      "springdoc.api-docs.path=/api-docs",
      "springdoc.swagger-ui.path=/swagger-ui",
      "avenirs.authentication.oidc.callback=/oidc/callback",
      "avenirs.authentication.oidc.callback.redirect=/oidc/callback/redirect",
      "avenirs.authentication.oidc.callback.profile=/oidc/callback/profile",
      "avenirs.authentication.oidc.callback.introspect=/oidc/callback/introspect",
      "management.actuator.health.path=/actuator/health",
      "avenirs.authentication.oidc.authorise.template.url=https://%s/cas/oidc/authorize?service=%s&code=%s",
      "avenirs.authentication.oidc.token.template.body=username=%s&password=%s",
      "avenirs.authentication.oidc.code.exchange.template.body=redirect_uri=https://%s/oidc/callback&code=%s",
      "avenirs.authentication.service.template=https://%s/oidc/callback",
      "avenirs.authentication.oidc.client.id=client",
      "avenirs.authentication.oidc.client.secret=secret",
      "avenirs.authentication.oidc.token.is.jwt=false"
    })
class OidcControllerIT {

  private static MockWebServer mockOidcServer;

  @Autowired private MockMvc mockMvc;

  @MockitoBean private BaseUserService baseUserService;

  @BeforeAll
  static void beforeAll() throws Exception {
    mockOidcServer = new MockWebServer();
    mockOidcServer.start();
  }

  @AfterAll
  static void afterAll() throws Exception {
    if (mockOidcServer != null) {
      mockOidcServer.shutdown();
    }
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "avenirs.authentication.oidc.token.url", () -> mockOidcServer.url("/token").toString());
    registry.add(
        "avenirs.authentication.oidc.provider.introspect.url",
        () -> mockOidcServer.url("/introspect?token=%s").toString());
    registry.add(
        "avenirs.authentication.oidc.provider.profile.url",
        () -> mockOidcServer.url("/profile?token=%s").toString());
  }

  @Test
  void oidcCallback_returnsAccessTokenPayload() throws Exception {
    mockOidcServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody(
                "{\"access_token\":\"AT\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"openid\",\"jwt\":false}"));

    mockMvc
        .perform(
            get("/oidc/callback").header("x-forwarded-host", "test-host.com").param("code", "code"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").value("AT"));
  }

  @Test
  void profile_activeToken_returnsProfilePayload() throws Exception {
    mockOidcServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"token\":\"AT\",\"active\":true,\"uniqueSecurityName\":\"gribonvald\"}"));

    mockOidcServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody(
                "{\"id\":\"id\",\"service\":\"svc\",\"attributes\":{\"given_name\":\"fn\",\"family_name\":\"ln\",\"email\":\"mail\"}}"));

    mockMvc
        .perform(post("/oidc/callback/profile").header("x-authorization", "AT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("id"))
        .andExpect(jsonPath("$.service").value("svc"))
        .andExpect(jsonPath("$.firstName").value("fn"))
        .andExpect(jsonPath("$.lastName").value("ln"))
        .andExpect(jsonPath("$.email").value("mail"));
  }

  @Test
  void introspect_returnsIntrospectionPayload() throws Exception {
    mockOidcServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"token\":\"AT\",\"active\":true,\"uniqueSecurityName\":\"gribonvald\"}"));

    mockMvc
        .perform(post("/oidc/callback/introspect").header("x-authorization", "AT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.userId").value("00000000-0000-0000-0000-000000000101"))
        .andExpect(jsonPath("$.uniqueSecurityName").value("gribonvald"));
  }

  @Test
  void redirect_returns302ToServiceUrl() throws Exception {
    mockMvc
        .perform(get("/oidc/callback/redirect").header("x-forwarded-host", "example.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "https://example.com/oidc/callback"));
  }
}
