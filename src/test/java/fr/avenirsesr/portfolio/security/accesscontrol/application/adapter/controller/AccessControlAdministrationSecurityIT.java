package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientOidcAuthenticationService;
import java.lang.reflect.Method;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
    scripts = {
      "classpath:db/test-fixtures-commons.sql",
      "classpath:db/test-fixtures-rbac-admin.sql"
    })
@Transactional
class AccessControlAdministrationSecurityIT {

  private static final String STUDENT_LOGIN = "patterson";
  private static final String PASSWORD = "password";

  private static final UUID GRANTABLE_ROLE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final String GRANTEE_LOGIN = "dugat";
  private static final UUID REVOCABLE_ASSIGNMENT_ID =
      UUID.fromString("00000000-0000-0000-0000-0000000000f6");

  @Value("${avenirs.access.control.grant}")
  private String grantEndpoint;

  @Value("${avenirs.access.control.revoke}")
  private String revokeEndpoint;

  @Autowired private MockMvc mockMvc;

  @Autowired private AccessTokenHelper accessTokenHelper;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private OIDCClientOidcAuthenticationService oidcClientAuthenticationService;

  @AfterEach
  void tearDown() {
    accessTokenHelper.clear();
  }

  @Nested
  class GivenTheGrantEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the /access-control/grant endpoint");
    }

    @Test
    void whenUnauthenticatedThenItShouldBeRejected() throws Exception {
      BddLogger.when("calling it without authentication");
      BddLogger.then("it should be rejected");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(grantEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(grantRequestBody()))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenAuthenticatedWithoutRbacPermissionThenItShouldBeForbidden() throws Exception {
      BddLogger.when("calling it as an authenticated student with no RBAC permission");
      BddLogger.then("it should return FORBIDDEN");

      String token = mockAccessToken(STUDENT_LOGIN);

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(grantEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(grantRequestBody()))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenCallerOnlyHasRbacReadThenItShouldBeForbidden() throws Exception {
      BddLogger.when("calling it as a principal holding only rbac:read");
      BddLogger.then("it should return FORBIDDEN");

      String token = mockAccessToken("rbacreader");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(grantEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(grantRequestBody()))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenCallerHasRbacAssignThenItShouldSucceed() throws Exception {
      BddLogger.when("calling it as a principal holding rbac:assign");
      BddLogger.then("it should be authorized");

      String token = mockAccessToken("rbacassigner");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(grantEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(grantRequestBody()))
          .andExpect(status().isOk());
    }

    @Test
    void whenCallerIsSuperAdminThenItShouldSucceed() throws Exception {
      BddLogger.when("calling it as the super administrator");
      BddLogger.then("it should be authorized");

      String token = mockAccessToken("superadmin");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(grantEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(grantRequestBody()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class GivenTheRevokeEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the /access-control/revoke endpoint");
    }

    @Test
    void whenUnauthenticatedThenItShouldBeRejected() throws Exception {
      BddLogger.when("calling it without authentication");
      BddLogger.then("it should be rejected");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(revokeEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(revokeRequestBody(REVOCABLE_ASSIGNMENT_ID)))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenAuthenticatedWithoutRbacPermissionThenItShouldBeForbidden() throws Exception {
      BddLogger.when("calling it as an authenticated student with no RBAC permission");
      BddLogger.then("it should return FORBIDDEN");

      String token = mockAccessToken(STUDENT_LOGIN);

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(revokeEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(revokeRequestBody(REVOCABLE_ASSIGNMENT_ID)))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenCallerOnlyHasRbacAssignThenItShouldBeForbidden() throws Exception {
      BddLogger.when("calling it as a principal holding only rbac:assign");
      BddLogger.then("it should return FORBIDDEN");

      String token = mockAccessToken("rbacassigner");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(revokeEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(revokeRequestBody(REVOCABLE_ASSIGNMENT_ID)))
          .andExpect(status().isForbidden());
    }

    @Test
    void whenCallerHasRbacRevokeThenItShouldSucceed() throws Exception {
      BddLogger.when("calling it as a principal holding rbac:revoke");
      BddLogger.then("it should be authorized");

      String token = mockAccessToken("rbacrevoker");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(revokeEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(revokeRequestBody(REVOCABLE_ASSIGNMENT_ID)))
          .andExpect(status().isOk());
    }

    @Test
    void whenCallerIsSuperAdminThenItShouldSucceed() throws Exception {
      BddLogger.when("calling it as the super administrator");
      BddLogger.then("it should be authorized");

      String token = mockAccessToken("superadmin");

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(revokeEndpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("x-authorization", token)
                  .content(revokeRequestBody(REVOCABLE_ASSIGNMENT_ID)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class GivenTheAnnotatedController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the AccessControlController @PreAuthorize annotations");
    }

    @Test
    void thenGrantAccessAuthorityShouldMatchPermRbacAssign() throws NoSuchMethodException {
      BddLogger.when("reading the grantAccess method annotation");
      BddLogger.then(
          "its hasAuthority value should be exactly EPermission.PERM_RBAC_ASSIGN.authority()");

      assertHasAuthorityValue(
          "grantAccess",
          new Class<?>[] {
            fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto
                .AccessControlGrantRequestDTO.class
          },
          EPermission.PERM_RBAC_ASSIGN.authority());
    }

    @Test
    void thenRevokeAccessAuthorityShouldMatchPermRbacRevoke() throws NoSuchMethodException {
      BddLogger.when("reading the revokeAccess method annotation");
      BddLogger.then(
          "its hasAuthority value should be exactly EPermission.PERM_RBAC_REVOKE.authority()");

      assertHasAuthorityValue(
          "revokeAccess",
          new Class<?>[] {
            fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto
                .AccessControlRevokeRequestDTO.class
          },
          EPermission.PERM_RBAC_REVOKE.authority());
    }

    private void assertHasAuthorityValue(
        String methodName, Class<?>[] parameterTypes, String expectedAuthority)
        throws NoSuchMethodException {
      Method method = AccessControlController.class.getMethod(methodName, parameterTypes);
      PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

      org.assertj.core.api.Assertions.assertThat(annotation).isNotNull();
      org.assertj.core.api.Assertions.assertThat(annotation.value())
          .isEqualTo("hasAuthority('" + expectedAuthority + "')");
    }
  }

  private String grantRequestBody() {
    return """
    {
      "login": "%s",
      "roleId": "%s"
    }
    """
        .formatted(GRANTEE_LOGIN, GRANTABLE_ROLE_ID);
  }

  private String revokeRequestBody(UUID assignmentId) {
    return """
    {
      "login": "%s",
      "assignmentId": "%s"
    }
    """
        .formatted(GRANTEE_LOGIN, assignmentId);
  }

  private String mockAccessToken(String login) {
    String token = "token-" + login;

    when(oidcClientAuthenticationService.getAccessToken(login, PASSWORD))
        .thenReturn(
            Optional.of(
                new OIDCAccessToken(token, token, "Bearer", 3600, "openid", null, null, false)));

    when(oidcService.introspectAccessToken(token))
        .thenReturn(new OIDCIntrospection(token, true, login));

    return accessTokenHelper.provideAccessToken(login, PASSWORD);
  }
}
