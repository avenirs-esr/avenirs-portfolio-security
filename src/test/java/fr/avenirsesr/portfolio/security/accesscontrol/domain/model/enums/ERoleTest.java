package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ERoleTest {

  @Nested
  class GivenTheRoleCatalog {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the ERole catalog");
    }

    @Nested
    class WhenInspectingEachRole {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("inspecting each role");
      }

      @ParameterizedTest
      @EnumSource(ERole.class)
      void thenItsPermissionsShouldBeImmutable(ERole role) {
        BddLogger.then("its flattened permissions set should be immutable");

        Set<EPermission> permissions = role.permissions();

        assertThrows(
            UnsupportedOperationException.class,
            () -> permissions.add(EPermission.PERM_PROFILE_READ_OWN));
      }

      @ParameterizedTest
      @EnumSource(ERole.class)
      void thenItsPermissionsShouldContainNoDuplicateAcrossDirectAndGroups(ERole role) {
        BddLogger.then("direct permissions and group permissions should not overlap");

        long expectedSize =
            role.directPermissions().size()
                + role.permissionGroups().stream()
                    .mapToLong(group -> group.permissions().size())
                    .sum();

        assertThat(role.permissions()).hasSize((int) expectedSize);
      }
    }

    @Nested
    class WhenFlatteningRoleStudent {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("flattening ROLE_STUDENT direct permissions and groups");
      }

      @Test
      void thenItShouldExposeTheExactPreRefactorPermissionSet() {
        BddLogger.then(
            "it should expose the exact permission set the role had before the refactor");

        assertThat(ERole.ROLE_STUDENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_PROFILE_READ_OWN,
                EPermission.PERM_PROFILE_UPDATE_OWN,
                EPermission.PERM_TRACE_CREATE_OWN,
                EPermission.PERM_TRACE_LIST_OWN,
                EPermission.PERM_TRACE_ASSOCIATION_MANAGE_OWN,
                EPermission.PERM_COMPETENCY_READ,
                EPermission.PERM_DECLARED_SKILL_LIST_OWN,
                EPermission.PERM_DECLARED_SKILL_CREATE_OWN,
                EPermission.PERM_DECLARED_SKILL_UPDATE_OWN,
                EPermission.PERM_DECLARED_SKILL_DELETE_OWN,
                EPermission.PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_LIST_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_CREATE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_DELETE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN,
                EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_REGISTER_OWN,
                EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_DOWNLOAD_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL,
                EPermission.PERM_EMPLOYMENT_KIT_READ_OWN,
                EPermission.PERM_FEEDBACK_REQUEST_CREATE_OWN,
                EPermission.PERM_FEEDBACK_RECEIVED_READ_OWN);
      }
    }

    @Nested
    class WhenFlatteningRoleStaff {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("flattening ROLE_STAFF direct permissions and groups");
      }

      @Test
      void thenItShouldExposeTheExactPreRefactorPermissionSet() {
        BddLogger.then(
            "it should expose the exact permission set the role had before the refactor");

        assertThat(ERole.ROLE_STAFF.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_PROFILE_READ_OWN,
                EPermission.PERM_PROFILE_UPDATE_OWN,
                EPermission.PERM_TRACE_READ_CONTEXTUAL,
                EPermission.PERM_TRACE_DOWNLOAD_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_LIBRARY_STAFF_READ,
                EPermission.PERM_ACTIVITY_PUBLISHED_UPDATE_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_REQUEST_READ_ASSIGNED_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_HISTORY_READ_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_DASHBOARD_READ_CONTEXTUAL);
      }
    }

    @Nested
    class WhenFlatteningRoleSuperAdmin {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("flattening ROLE_SUPER_ADMIN groups (no direct permissions)");
      }

      @Test
      void thenItShouldExposeTheExactPreRefactorPermissionSet() {
        BddLogger.then(
            "it should expose the exact permission set the role had before the refactor");

        assertThat(ERole.ROLE_SUPER_ADMIN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_PRIMARY_ESTABLISHMENT_READ,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_CREATE,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_UPDATE,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_DELETE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_READ,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_CREATE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_UPDATE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_DELETE,
                EPermission.PERM_GROUP_READ,
                EPermission.PERM_GROUP_IMPORT,
                EPermission.PERM_GROUP_CREATE,
                EPermission.PERM_GROUP_UPDATE,
                EPermission.PERM_GROUP_DELETE,
                EPermission.PERM_RBAC_READ,
                EPermission.PERM_RBAC_ASSIGN,
                EPermission.PERM_RBAC_REVOKE,
                EPermission.PERM_RBAC_MANAGE);
      }
    }

    @Nested
    class WhenCheckingRbacAdministrationPermissions {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking which roles carry RBAC administration permissions");
      }

      @Test
      void thenOnlySuperAdminShouldHaveThem() {
        BddLogger.then("ROLE_SUPER_ADMIN should have all four, and no other role should have any");

        assertThat(ERole.ROLE_SUPER_ADMIN.permissions())
            .contains(
                EPermission.PERM_RBAC_READ,
                EPermission.PERM_RBAC_ASSIGN,
                EPermission.PERM_RBAC_REVOKE,
                EPermission.PERM_RBAC_MANAGE);

        assertThat(ERole.ROLE_STUDENT.permissions())
            .noneMatch(permission -> permission.name().startsWith("PERM_RBAC_"));
        assertThat(ERole.ROLE_STAFF.permissions())
            .noneMatch(permission -> permission.name().startsWith("PERM_RBAC_"));
      }
    }

    @Nested
    class WhenARoleDeclaresAPermissionBothDirectlyAndViaAGroup {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("a role declares a permission both directly and via a group");
      }

      @Test
      void thenFlatteningShouldFailExplicitly() throws NoSuchMethodException {
        BddLogger.then("flattening should fail explicitly instead of silently deduplicating");

        Method flatten = ERole.class.getDeclaredMethod("flatten", Set.class, Set.class);
        flatten.setAccessible(true);

        InvocationTargetException thrown =
            assertThrows(
                InvocationTargetException.class,
                () ->
                    flatten.invoke(
                        null,
                        Set.of(EPermission.PERM_GROUP_READ),
                        Set.of(EPermissionGroup.GROUP_MANAGEMENT)));

        assertThat(thrown.getCause()).isInstanceOf(IllegalStateException.class);
      }
    }
  }
}
