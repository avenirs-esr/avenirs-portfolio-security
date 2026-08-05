package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = SpringSecurityConfigTest.TestController.class)
@Import(SpringSecurityConfig.class)
@TestPropertySource(
    properties = {
      "springdoc.api-docs.path=/api-docs",
      "springdoc.swagger-ui.path=/swagger-ui",
      "avenirs.authentication.oidc.callback=/oidc/callback",
      "avenirs.authentication.oidc.callback.redirect=/oidc/redirect",
      "management.actuator.health.path=/actuator/health"
    })
class SpringSecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private BaseUserService baseUserService;

  @MockitoBean private PrincipalGrantedAuthoritiesService principalGrantedAuthoritiesService;

  @MockitoBean private PrincipalRepository principalRepository;

  @RestController
  static class TestController {

    @GetMapping("/swagger-ui/index.html")
    ResponseEntity<Void> swaggerUi() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/api-docs")
    ResponseEntity<Void> apiDocs() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    ResponseEntity<Void> login() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/oidc/callback")
    ResponseEntity<Void> oidcCallback() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/oidc/redirect")
    ResponseEntity<Void> oidcRedirect() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/actuator/health")
    ResponseEntity<Void> actuatorHealth() {
      return ResponseEntity.ok().build();
    }

    @GetMapping("/anything-protected")
    ResponseEntity<Void> protectedEndpoint() {
      return ResponseEntity.ok().build();
    }
  }

  @Test
  void publicPath_isPermitted() throws Exception {
    BddLogger.given("a security configuration with a public filter chain");

    BddLogger.when("calling a public path without authentication");

    BddLogger.then("it should not be rejected by Spring Security");
    String[] publicPaths = {
      "/swagger-ui/index.html",
      "/api-docs",
      "/login",
      "/oidc/callback",
      "/oidc/redirect",
      "/actuator/health"
    };

    for (String path : publicPaths) {
      int httpStatus = mockMvc.perform(get(path)).andReturn().getResponse().getStatus();
      assertThat(httpStatus)
          .as("Public path %s should not be rejected by Spring Security", path)
          .isNotIn(401, 403);
    }
  }

  @Test
  void protectedPath_requiresAuthentication() throws Exception {
    BddLogger.given("a security configuration with a protected filter chain");

    BddLogger.when("calling a protected path without authentication");

    BddLogger.then("it should be rejected by Spring Security");
    mockMvc.perform(get("/anything-protected")).andExpect(status().isForbidden());
  }
}
