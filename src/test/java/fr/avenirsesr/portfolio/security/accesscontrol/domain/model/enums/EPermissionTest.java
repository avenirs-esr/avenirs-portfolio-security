package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EPermissionTest {

  /** lowercase, colon-separated segments, each alphanumeric with optional internal hyphens. */
  private static final Pattern AUTHORITY_CONVENTION =
      Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*(:[a-z0-9]+(-[a-z0-9]+)*)+$");

  @Nested
  class GivenThePermissionCatalog {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the EPermission catalog");
    }

    @Nested
    class WhenInspectingEachPermission {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("inspecting each permission");
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsAuthorityShouldNotBeBlank(EPermission permission) {
        BddLogger.then("its authority code should not be blank");

        assertThat(permission.authority()).isNotBlank();
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsDescriptionShouldNotBeBlank(EPermission permission) {
        BddLogger.then("its description should not be blank");

        assertThat(permission.description()).isNotBlank();
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsAuthorityShouldNotStartWithPerm(EPermission permission) {
        BddLogger.then("its authority code should not start with PERM_");

        assertThat(permission.authority()).doesNotStartWith("PERM_").doesNotStartWith("perm_");
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsAuthorityShouldMatchTheNamingConvention(EPermission permission) {
        BddLogger.then("its authority code should match the resource:action[:scope] convention");

        assertThat(permission.authority()).matches(AUTHORITY_CONVENTION);
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsAuthorityShouldBeIndependentOfItsEnumName(EPermission permission) {
        BddLogger.then("its authority code should not equal the Java constant name");

        assertThat(permission.authority()).isNotEqualTo(permission.name());
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenItsLegacyAuthorityShouldBeItsEnumName(EPermission permission) {
        BddLogger.then("its legacy authority should be its enum name (pre-uniformization value)");

        assertThat(permission.legacyAuthority()).isEqualTo(permission.name());
      }
    }

    @Nested
    class WhenCheckingUniqueness {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking uniqueness across the whole catalog");
      }

      @Test
      void thenNoTwoPermissionsShouldShareTheSameAuthority() {
        BddLogger.then("no two permissions should share the same authority");

        long distinctAuthorities =
            Arrays.stream(EPermission.values()).map(EPermission::authority).distinct().count();

        assertThat(distinctAuthorities).isEqualTo(EPermission.values().length);
      }
    }

    @Nested
    class WhenCheckingTheFourRbacAdministrationPermissions {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking the four RBAC administration permissions");
      }

      @Test
      void thenTheyShouldExposeTheExpectedAuthorityCodes() {
        BddLogger.then("they should expose the rbac:* authority codes, distinct from their name");

        assertThat(EPermission.PERM_RBAC_READ.authority()).isEqualTo("rbac:read");
        assertThat(EPermission.PERM_RBAC_ASSIGN.authority()).isEqualTo("rbac:assign");
        assertThat(EPermission.PERM_RBAC_REVOKE.authority()).isEqualTo("rbac:revoke");
        assertThat(EPermission.PERM_RBAC_MANAGE.authority()).isEqualTo("rbac:manage");
      }
    }

    @Nested
    class WhenCheckingTheExactAuthorityOfEveryPermission {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("mapping every permission to its expected authority");
      }

      @Test
      void thenEveryPermissionShouldExposeExactlyItsExpectedAuthority() {
        BddLogger.then("every permission's authority should match the expected catalog exactly");

        Map<EPermission, String> expected =
            Map.ofEntries(
                Map.entry(EPermission.PERM_PROFILE_READ_OWN, "profile:read:own"),
                Map.entry(EPermission.PERM_PROFILE_UPDATE_OWN, "profile:update:own"),
                Map.entry(EPermission.PERM_TRACE_CREATE_OWN, "trace:create:own"),
                Map.entry(EPermission.PERM_TRACE_LIST_OWN, "trace:list:own"),
                Map.entry(EPermission.PERM_TRACE_READ_CONTEXTUAL, "trace:read:contextual"),
                Map.entry(EPermission.PERM_TRACE_DOWNLOAD_CONTEXTUAL, "trace:download:contextual"),
                Map.entry(
                    EPermission.PERM_TRACE_ASSOCIATION_MANAGE_OWN, "trace:association:manage:own"),
                Map.entry(EPermission.PERM_COMPETENCY_READ, "competency:read"),
                Map.entry(EPermission.PERM_DECLARED_SKILL_LIST_OWN, "declared-skill:list:own"),
                Map.entry(EPermission.PERM_DECLARED_SKILL_CREATE_OWN, "declared-skill:create:own"),
                Map.entry(EPermission.PERM_DECLARED_SKILL_UPDATE_OWN, "declared-skill:update:own"),
                Map.entry(EPermission.PERM_DECLARED_SKILL_DELETE_OWN, "declared-skill:delete:own"),
                Map.entry(
                    EPermission.PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN,
                    "declared-skill:association:manage:own"),
                Map.entry(
                    EPermission.PERM_DECLARED_EXPERIENCE_LIST_OWN, "declared-experience:list:own"),
                Map.entry(
                    EPermission.PERM_DECLARED_EXPERIENCE_CREATE_OWN,
                    "declared-experience:create:own"),
                Map.entry(
                    EPermission.PERM_DECLARED_EXPERIENCE_DELETE_OWN,
                    "declared-experience:delete:own"),
                Map.entry(
                    EPermission.PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN,
                    "declared-experience:association:manage:own"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
                    "activity:catalog:read:contextual"),
                Map.entry(EPermission.PERM_ACTIVITY_REGISTER_OWN, "activity:register:own"),
                Map.entry(EPermission.PERM_ACTIVITY_READ_CONTEXTUAL, "activity:read:contextual"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_DOCUMENT_DOWNLOAD_CONTEXTUAL,
                    "activity:document:download:contextual"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL,
                    "activity:document:read:contextual"),
                Map.entry(EPermission.PERM_EMPLOYMENT_KIT_READ_OWN, "employment-kit:read:own"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_LIBRARY_STAFF_READ, "activity:library:staff:read"),
                Map.entry(EPermission.PERM_ACTIVITY_NATIONAL_CREATE, "activity:national:create"),
                Map.entry(EPermission.PERM_ACTIVITY_NATIONAL_UPDATE, "activity:national:update"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_PUBLISHED_UPDATE_CONTEXTUAL,
                    "activity:published:update:contextual"),
                Map.entry(EPermission.PERM_ACTIVITY_NATIONAL_DELETE, "activity:national:delete"),
                Map.entry(
                    EPermission.PERM_ACTIVITY_FEEDBACK_SETTINGS_UPDATE,
                    "activity:feedback-settings:update"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_REQUEST_CREATE_OWN, "feedback:request:create:own"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_RECEIVED_READ_OWN, "feedback:received:read:own"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_REQUEST_READ_ASSIGNED_CONTEXTUAL,
                    "feedback:request:read:assigned"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED_CONTEXTUAL,
                    "feedback:request:respond:assigned"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_HISTORY_READ_CONTEXTUAL,
                    "feedback:history:read:contextual"),
                Map.entry(
                    EPermission.PERM_FEEDBACK_DASHBOARD_READ_CONTEXTUAL,
                    "feedback:dashboard:read:contextual"),
                Map.entry(
                    EPermission.PERM_PRIMARY_ESTABLISHMENT_READ, "primary-establishment:read"),
                Map.entry(
                    EPermission.PERM_PRIMARY_ESTABLISHMENT_CREATE, "primary-establishment:create"),
                Map.entry(
                    EPermission.PERM_PRIMARY_ESTABLISHMENT_UPDATE, "primary-establishment:update"),
                Map.entry(
                    EPermission.PERM_PRIMARY_ESTABLISHMENT_DELETE, "primary-establishment:delete"),
                Map.entry(
                    EPermission.PERM_SECONDARY_ESTABLISHMENT_READ, "secondary-establishment:read"),
                Map.entry(
                    EPermission.PERM_SECONDARY_ESTABLISHMENT_CREATE,
                    "secondary-establishment:create"),
                Map.entry(
                    EPermission.PERM_SECONDARY_ESTABLISHMENT_UPDATE,
                    "secondary-establishment:update"),
                Map.entry(
                    EPermission.PERM_SECONDARY_ESTABLISHMENT_DELETE,
                    "secondary-establishment:delete"),
                Map.entry(EPermission.PERM_GROUP_READ, "group:read"),
                Map.entry(EPermission.PERM_GROUP_IMPORT, "group:import"),
                Map.entry(EPermission.PERM_GROUP_CREATE, "group:create"),
                Map.entry(EPermission.PERM_GROUP_UPDATE, "group:update"),
                Map.entry(EPermission.PERM_GROUP_DELETE, "group:delete"),
                Map.entry(EPermission.PERM_RBAC_READ, "rbac:read"),
                Map.entry(EPermission.PERM_RBAC_ASSIGN, "rbac:assign"),
                Map.entry(EPermission.PERM_RBAC_REVOKE, "rbac:revoke"),
                Map.entry(EPermission.PERM_RBAC_MANAGE, "rbac:manage"));

        assertThat(expected).containsOnlyKeys(EPermission.values());

        Map<EPermission, String> actual =
            Arrays.stream(EPermission.values())
                .collect(Collectors.toMap(permission -> permission, EPermission::authority));

        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
      }
    }
  }
}
