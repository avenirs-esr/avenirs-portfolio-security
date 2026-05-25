package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientOidcAuthenticationService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
    scripts = {
      "classpath:db/test-fixtures-commons.sql",
      "classpath:db/test-fixtures-rbac-case1.sql"
    })
@Transactional
class AccessControlControllerIT {

  private static final UUID ACT_SHARE_READ =
      UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ACT_SHARE_WRITE =
      UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID ACT_DISPLAY = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final UUID ACT_EDIT = UUID.fromString("00000000-0000-0000-0000-000000000004");
  private static final UUID ACT_FEEDBACK = UUID.fromString("00000000-0000-0000-0000-000000000005");
  private static final UUID ACT_DELETE = UUID.fromString("00000000-0000-0000-0000-000000000006");

  @Value("${avenirs.test.rbac.case1.user.login}")
  private String ownerLogin;

  @Value("${avenirs.test.rbac.case1.user.password}")
  private String ownerPassword;

  @Value("${avenirs.test.rbac.case1.authorized.resource.id}")
  private String authorizedResourceId;

  @Value("${avenirs.test.rbac.case1.unauthorized.resource.id}")
  private String unauthorizedResourceId;

  @Value("${avenirs.test.rbac.unprivileged.user.login}")
  private String unprivilegedLogin;

  @Value("${avenirs.test.rbac.unprivileged.user.password}")
  private String unprivilegedPassword;

  @Value("${avenirs.access.control.authorize}")
  private String authorizeEndPoint;

  @Autowired private MockMvc mockMvc;

  @Autowired private AccessTokenHelper accessTokenHelper;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private OIDCClientOidcAuthenticationService oidcClientAuthenticationService;

  @AfterEach
  void tearDown() {
    accessTokenHelper.clear();
  }

  @Nested
  class GivenAccessControlEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an access control endpoint");
    }

    @Nested
    class AndAnOwnerUser {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("an owner user");
      }

      @Nested
      class WhenAccessingAnAuthorizedResource {

        @BeforeEach
        void setupWhen() {
          BddLogger.when("accessing an authorized resource");
        }

        @Test
        void thenItShouldAuthorizeEveryExpectedAction() throws Exception {
          BddLogger.then("it should authorize every expected action");

          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_SHARE_READ,
              status().is2xxSuccessful());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_SHARE_WRITE,
              status().is2xxSuccessful());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_DISPLAY,
              status().is2xxSuccessful());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_EDIT,
              status().is2xxSuccessful());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_FEEDBACK,
              status().is2xxSuccessful());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              authorizedResourceId,
              ACT_DELETE,
              status().is2xxSuccessful());
        }
      }

      @Nested
      class WhenAccessingAnUnauthorizedResource {

        @BeforeEach
        void setupWhen() {
          BddLogger.when("accessing an unauthorized resource");
        }

        @Test
        void thenItShouldForbidEveryExpectedAction() throws Exception {
          BddLogger.then("it should forbid every expected action");

          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              unauthorizedResourceId,
              ACT_SHARE_READ,
              status().isForbidden());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              unauthorizedResourceId,
              ACT_SHARE_WRITE,
              status().isForbidden());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              unauthorizedResourceId,
              ACT_DISPLAY,
              status().isForbidden());
          expectAuthorizationStatus(
              ownerLogin, ownerPassword, unauthorizedResourceId, ACT_EDIT, status().isForbidden());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              unauthorizedResourceId,
              ACT_FEEDBACK,
              status().isForbidden());
          expectAuthorizationStatus(
              ownerLogin,
              ownerPassword,
              unauthorizedResourceId,
              ACT_DELETE,
              status().isForbidden());
        }
      }
    }

    @Nested
    class AndAnUnprivilegedUser {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("an unprivileged user");
      }

      @Nested
      class WhenAccessingAnAuthorizedResource {

        @BeforeEach
        void setupWhen() {
          BddLogger.when("accessing an authorized resource");
        }

        @Test
        void thenItShouldForbidEveryExpectedAction() throws Exception {
          BddLogger.then("it should forbid every expected action");

          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_SHARE_READ,
              status().isForbidden());
          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_SHARE_WRITE,
              status().isForbidden());
          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_DISPLAY,
              status().isForbidden());
          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_EDIT,
              status().isForbidden());
          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_FEEDBACK,
              status().isForbidden());
          expectAuthorizationStatus(
              unprivilegedLogin,
              unprivilegedPassword,
              authorizedResourceId,
              ACT_DELETE,
              status().isForbidden());
        }
      }
    }
  }

  private void expectAuthorizationStatus(
      String login, String password, String resourceId, UUID actionId, ResultMatcher expectedStatus)
      throws Exception {
    String token = mockAccessToken(login, password);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(authorizeEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", token)
                .param("login", login)
                .param("actionId", actionId.toString())
                .param("resourceId", resourceId))
        .andExpect(expectedStatus);
  }

  private String mockAccessToken(String login, String password) throws Exception {
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
