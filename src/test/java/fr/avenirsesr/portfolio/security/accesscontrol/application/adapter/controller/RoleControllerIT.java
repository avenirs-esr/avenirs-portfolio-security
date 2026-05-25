package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientOidcAuthenticationService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RoleControllerIT {

  private static final String USER_WITHOUT_ROLE_LOGIN = "patterson";
  private static final String USER_WITH_ROLE_LOGIN = "deman";
  private static final String PASSWORD = "password";

  @Value("${avenirs.access.control.roles}")
  private String rolesEndpoint;

  @Autowired private MockMvc mockMvc;

  @Autowired private AccessTokenHelper accessTokenHelper;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private OIDCClientOidcAuthenticationService oidcClientAuthenticationService;

  @AfterEach
  void tearDown() {
    accessTokenHelper.clear();
  }

  @Nested
  class GivenRolesEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a roles endpoint");
    }

    @Nested
    class WhenGettingRolesWithoutAuthentication {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting roles without authentication");
      }

      @Test
      void thenItShouldReturnForbidden() throws Exception {
        BddLogger.then("it should return FORBIDDEN");

        mockMvc
            .perform(get(rolesEndpoint).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
      }
    }

    @Nested
    class WhenGettingRolesWithInvalidToken {
      private String token;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting roles with an invalid token");

        token = "invalid-token";

        when(oidcService.introspectAccessToken(token))
            .thenReturn(new OIDCIntrospection(token, false, null));
      }

      @Test
      void thenItShouldReturnForbidden() throws Exception {
        BddLogger.then("it should return FORBIDDEN");

        mockMvc
            .perform(
                get(rolesEndpoint)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-authorization", token))
            .andExpect(status().isForbidden());
      }
    }

    @Nested
    class WhenGettingRolesWithValidToken {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting roles with a valid token");
      }

      @Nested
      class AndTheUserHasNoRole {
        private String token;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the user has no role");

          token = mockAccessToken(USER_WITHOUT_ROLE_LOGIN, PASSWORD);
        }

        @Test
        void thenItShouldReturnEmptyList() throws Exception {
          BddLogger.then("it should return an empty list");

          mockMvc
              .perform(
                  get(rolesEndpoint)
                      .accept(MediaType.APPLICATION_JSON)
                      .header("x-authorization", token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(0)));
        }
      }

      @Nested
      @Sql(
          scripts = {
            "classpath:db/test-fixtures-commons.sql",
            "classpath:db/test-fixtures-role-controller.sql"
          })
      class AndTheUserHasRoles {
        private String token;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the user has roles");

          token = mockAccessToken(USER_WITH_ROLE_LOGIN, PASSWORD);
        }

        @Test
        void thenItShouldReturnUserRoles() throws Exception {
          BddLogger.then("it should return user roles");

          mockMvc
              .perform(
                  get(rolesEndpoint)
                      .accept(MediaType.APPLICATION_JSON)
                      .header("x-authorization", token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(2)))
              .andExpect(jsonPath("$[*].name", containsInAnyOrder("ROLE_PAIR", "ROLE_CONTRIBUTOR")))
              .andExpect(
                  jsonPath(
                      "$[*].description",
                      containsInAnyOrder("Can give feedback", "Contributor for the resource")));
        }
      }
    }
  }

  private String mockAccessToken(String login, String password) {
    String token = "token-" + login;

    when(oidcClientAuthenticationService.getAccessToken(login, password))
        .thenReturn(
            Optional.of(
                new OIDCAccessToken(token, token, "Bearer", 3600, "openid", null, null, false)));

    when(oidcService.introspectAccessToken(token))
        .thenReturn(new OIDCIntrospection(token, true, login));

    return accessTokenHelper.provideAccessToken(login, password);
  }
}
