package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantPermissionsRequestDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientOidcAuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
class AccessControlGrantPermissionsIT {

  private static final String STUDENT_LOGIN = "patterson";
  private static final String PASSWORD = "password";
  private static final String GRANTEE_LOGIN = "dugat";

  private static final String RBAC_READ = "rbac:read";
  private static final Set<String> RBAC_MANAGEMENT_AUTHORITIES =
      Set.of("rbac:read", "rbac:assign", "rbac:revoke", "rbac:manage");

  @Value("${avenirs.access.control.grant-permissions}")
  private String grantPermissionsEndpoint;

  @Autowired private MockMvc mockMvc;

  @Autowired private AccessTokenHelper accessTokenHelper;

  @Autowired private PrincipalGrantedAuthoritiesService authoritiesService;

  @MockitoBean private OidcService oidcService;

  @MockitoBean private OIDCClientOidcAuthenticationService oidcClientAuthenticationService;

  @AfterEach
  void tearDown() {
    accessTokenHelper.clear();
  }

  @Nested
  class GivenTheGrantPermissionsEndpoint {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the /access-control/grant-permissions endpoint");
    }

    @Nested
    class WhenCallingItWithoutTheRequiredAuthority {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("calling it without the rbac:assign authority");
      }

      @Test
      void thenAnUnauthenticatedCallerShouldBeRejected() throws Exception {
        BddLogger.then("an unauthenticated caller should be rejected");

        mockMvc
            .perform(
                MockMvcRequestBuilders.post(grantPermissionsEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody(RBAC_READ)))
            .andExpect(status().isForbidden());
      }

      @Test
      void thenAStudentShouldBeForbidden() throws Exception {
        BddLogger.then("a student should be forbidden");

        callAs(STUDENT_LOGIN, requestBody(RBAC_READ)).andExpect(status().isForbidden());

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN)).doesNotContain(RBAC_READ);
      }

      @Test
      void thenACallerHoldingOnlyRbacReadShouldBeForbidden() throws Exception {
        BddLogger.then("a caller holding only rbac:read should be forbidden");

        callAs("rbacreader", requestBody(RBAC_READ)).andExpect(status().isForbidden());

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN)).doesNotContain(RBAC_READ);
      }

      @Test
      void thenACallerHoldingOnlyRbacRevokeShouldBeForbidden() throws Exception {
        BddLogger.then("a caller holding only rbac:revoke should be forbidden");

        callAs("rbacrevoker", requestBody(RBAC_READ)).andExpect(status().isForbidden());
      }
    }

    @Nested
    class WhenGrantingASinglePermission {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("a caller holding rbac:assign grants a single permission");
      }

      @Test
      void thenThePermissionShouldBeAddedToThePrincipalAuthorities() throws Exception {
        BddLogger.then("the permission should be added to the principal authorities");

        Set<String> before = authoritiesService.resolveAuthorities(GRANTEE_LOGIN);

        callAs("rbacassigner", requestBody(RBAC_READ))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value(GRANTEE_LOGIN))
            .andExpect(jsonPath("$.granted").value(true))
            .andExpect(jsonPath("$.assignmentId").isNotEmpty())
            .andExpect(jsonPath("$.error").value(nullValue()));

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN))
            .containsAll(before)
            .contains(RBAC_READ);
      }

      @Test
      void thenTheSuperAdministratorShouldBeAllowedToo() throws Exception {
        BddLogger.then("the super administrator should be allowed too");

        callAs("superadmin", requestBody(RBAC_READ))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.granted").value(true));
      }
    }

    @Nested
    class WhenGrantingAPermissionGroup {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("a caller holding rbac:assign grants a permission group");
      }

      @Test
      void thenEveryPermissionOfTheGroupShouldBeAddedToThePrincipalAuthorities() throws Exception {
        BddLogger.then("every permission of the group should be added to the principal");

        Set<String> before = authoritiesService.resolveAuthorities(GRANTEE_LOGIN);

        callAs("rbacassigner", requestBody(null, "RBAC_MANAGEMENT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.granted").value(true));

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN))
            .containsAll(before)
            .containsAll(RBAC_MANAGEMENT_AUTHORITIES);
      }

      @Test
      void thenPermissionsAndGroupsShouldBeMergedInASingleAssignment() throws Exception {
        BddLogger.then("permissions and groups should be merged in a single assignment");

        callAs("rbacassigner", requestBody("rbac:assign", "RBAC_MANAGEMENT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.granted").value(true));

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN))
            .containsAll(RBAC_MANAGEMENT_AUTHORITIES);
      }
    }

    @Nested
    class WhenTheRequestIsInvalid {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("a caller holding rbac:assign sends an invalid request");
      }

      @Test
      void thenAnUnknownPermissionShouldBeRefused() throws Exception {
        BddLogger.then("an unknown permission should be refused");

        Set<String> before = authoritiesService.resolveAuthorities(GRANTEE_LOGIN);

        callAs("rbacassigner", requestBody("unknown:permission"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.granted").value(false))
            .andExpect(jsonPath("$.error").value(containsString("unknown:permission")));

        assertThat(authoritiesService.resolveAuthorities(GRANTEE_LOGIN)).isEqualTo(before);
      }

      @Test
      void thenAnUnknownPermissionGroupShouldBeRefused() throws Exception {
        BddLogger.then("an unknown permission group should be refused");

        callAs("rbacassigner", requestBody(null, "UNKNOWN_GROUP"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.granted").value(false))
            .andExpect(jsonPath("$.error").value(containsString("UNKNOWN_GROUP")));
      }

      @Test
      void thenARequestWithoutPermissionNorGroupShouldBeRefused() throws Exception {
        BddLogger.then("a request without permission nor group should be refused");

        callAs("rbacassigner", requestBody(null, null))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.granted").value(false))
            .andExpect(jsonPath("$.error").isNotEmpty());
      }

      @Test
      void thenAnUnknownLoginShouldBeRefused() throws Exception {
        BddLogger.then("an unknown login should be refused");

        callAs(
                "rbacassigner",
                """
                {
                  "login": "nobody",
                  "permissions": ["%s"]
                }
                """
                    .formatted(RBAC_READ))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.granted").value(false))
            .andExpect(jsonPath("$.error").value(containsString("nobody")));
      }
    }
  }

  @Nested
  class GivenTheAnnotatedController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the AccessControlController grantPermissions method");
    }

    @Test
    void thenItsAuthorityShouldMatchPermRbacAssign() throws NoSuchMethodException {
      BddLogger.when("reading its @PreAuthorize annotation");
      BddLogger.then("it should require exactly EPermission.PERM_RBAC_ASSIGN.authority()");

      Method method =
          AccessControlController.class.getMethod(
              "grantPermissions", AccessControlGrantPermissionsRequestDTO.class);
      PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

      assertThat(annotation).isNotNull();
      assertThat(annotation.value())
          .isEqualTo("hasAuthority('" + EPermission.PERM_RBAC_ASSIGN.authority() + "')");
    }
  }

  private ResultActions callAs(String callerLogin, String body) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.post(grantPermissionsEndpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .header("x-authorization", mockAccessToken(callerLogin))
            .content(body));
  }

  private String requestBody(String permission) {
    return requestBody(permission, null);
  }

  private String requestBody(String permission, String permissionGroup) {
    return """
    {
      "login": "%s",
      "permissions": [%s],
      "permissionGroups": [%s]
    }
    """
        .formatted(GRANTEE_LOGIN, quoted(permission), quoted(permissionGroup));
  }

  private String quoted(String value) {
    return value == null ? "" : "\"" + value + "\"";
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
