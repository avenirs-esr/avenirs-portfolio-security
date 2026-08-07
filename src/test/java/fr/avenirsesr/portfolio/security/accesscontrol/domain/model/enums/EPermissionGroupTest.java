package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EPermissionGroupTest {

  @Nested
  class GivenThePermissionGroupCatalog {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the EPermissionGroup catalog");
    }

    @Nested
    class WhenInspectingEachGroup {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("inspecting each group");
      }

      @ParameterizedTest
      @EnumSource(EPermissionGroup.class)
      void thenItShouldNotBeEmpty(EPermissionGroup group) {
        BddLogger.then("it should not be empty");

        assertThat(group.permissions()).isNotEmpty();
      }

      @ParameterizedTest
      @EnumSource(EPermissionGroup.class)
      void thenItShouldNotContainANullPermission(EPermissionGroup group) {
        BddLogger.then("it should not contain a null permission");

        assertThat(group.permissions()).doesNotContainNull();
      }

      @ParameterizedTest
      @EnumSource(EPermissionGroup.class)
      void thenItsPermissionsShouldBeImmutable(EPermissionGroup group) {
        BddLogger.then("its permissions set should be immutable");

        Set<EPermission> permissions = group.permissions();

        assertThrows(
            UnsupportedOperationException.class,
            () -> permissions.add(EPermission.PERM_PROFILE_READ_OWN));
      }

      @ParameterizedTest
      @EnumSource(EPermissionGroup.class)
      void thenItShouldBeUsedByAtLeastOneRole(EPermissionGroup group) {
        BddLogger.then("it should be used by at least one role");

        boolean used =
            Stream.of(ERole.values()).anyMatch(role -> role.permissionGroups().contains(group));

        assertThat(used).as("group %s must be referenced by at least one ERole", group).isTrue();
      }
    }

    @Nested
    class WhenCheckingExactContent {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking the exact content of each group");
      }

      @Test
      void thenEstablishmentManagementShouldContainExactlyEstablishmentPermissions() {
        BddLogger.then(
            "ESTABLISHMENT_MANAGEMENT should contain exactly the establishment CRUD permissions");

        assertThat(EPermissionGroup.ESTABLISHMENT_MANAGEMENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_PRIMARY_ESTABLISHMENT_READ,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_CREATE,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_UPDATE,
                EPermission.PERM_PRIMARY_ESTABLISHMENT_DELETE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_READ,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_CREATE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_UPDATE,
                EPermission.PERM_SECONDARY_ESTABLISHMENT_DELETE);
      }

      @Test
      void thenGroupManagementShouldContainExactlyGroupPermissions() {
        BddLogger.then("GROUP_MANAGEMENT should contain exactly the group CRUD permissions");

        assertThat(EPermissionGroup.GROUP_MANAGEMENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_GROUP_READ,
                EPermission.PERM_GROUP_IMPORT,
                EPermission.PERM_GROUP_CREATE,
                EPermission.PERM_GROUP_UPDATE,
                EPermission.PERM_GROUP_DELETE);
      }

      @Test
      void thenActivityManagementShouldContainExactlyActivityCrudPermissions() {
        BddLogger.then(
            "ACTIVITY_MANAGEMENT should contain exactly the activity create/update/delete"
                + " permissions");

        assertThat(EPermissionGroup.ACTIVITY_MANAGEMENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_ACTIVITY_CREATE,
                EPermission.PERM_ACTIVITY_UPDATE,
                EPermission.PERM_ACTIVITY_DELETE);
      }

      @Test
      void thenTraceManagementOwnShouldContainExactlyOwnTracePermissions() {
        BddLogger.then("TRACE_MANAGEMENT_OWN should contain exactly the own-trace permissions");

        assertThat(EPermissionGroup.TRACE_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_TRACE_CREATE_OWN,
                EPermission.PERM_TRACE_LIST_OWN,
                EPermission.PERM_TRACE_UPDATE_OWN,
                EPermission.PERM_TRACE_DELETE_OWN,
                EPermission.PERM_TRACE_ASSOCIATION_MANAGE_OWN);
      }

      @Test
      void thenDeclaredActivityManagementOwnShouldContainExactlyOwnActivityPermissions() {
        BddLogger.then(
            "DECLARED_ACTIVITY_MANAGEMENT_OWN should contain exactly the own declared-activity"
                + " permissions");

        assertThat(EPermissionGroup.DECLARED_ACTIVITY_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_DECLARED_ACTIVITY_LIST_OWN,
                EPermission.PERM_DECLARED_ACTIVITY_UPDATE_OWN,
                EPermission.PERM_DECLARED_ACTIVITY_ASSOCIATION_MANAGE_OWN);
      }

      @Test
      void thenDeclaredSkillManagementOwnShouldContainExactlyOwnSkillPermissions() {
        BddLogger.then(
            "DECLARED_SKILL_MANAGEMENT_OWN should contain exactly the own declared-skill"
                + " permissions");

        assertThat(EPermissionGroup.DECLARED_SKILL_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_DECLARED_SKILL_LIST_OWN,
                EPermission.PERM_DECLARED_SKILL_CREATE_OWN,
                EPermission.PERM_DECLARED_SKILL_UPDATE_OWN,
                EPermission.PERM_DECLARED_SKILL_DELETE_OWN,
                EPermission.PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN);
      }

      @Test
      void thenDeclaredExperienceManagementOwnShouldContainExactlyOwnExperiencePermissions() {
        BddLogger.then(
            "DECLARED_EXPERIENCE_MANAGEMENT_OWN should contain exactly the own declared-experience"
                + " permissions");

        assertThat(EPermissionGroup.DECLARED_EXPERIENCE_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_DECLARED_EXPERIENCE_LIST_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_CREATE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_UPDATE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_DELETE_OWN,
                EPermission.PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN);
      }

      @Test
      void thenDeclaredProgramManagementOwnShouldContainExactlyOwnProgramPermissions() {
        BddLogger.then(
            "DECLARED_PROGRAM_MANAGEMENT_OWN should contain exactly the own declared-program"
                + " permissions");

        assertThat(EPermissionGroup.DECLARED_PROGRAM_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_DECLARED_PROGRAM_LIST_OWN,
                EPermission.PERM_DECLARED_PROGRAM_CREATE_OWN,
                EPermission.PERM_DECLARED_PROGRAM_UPDATE_OWN,
                EPermission.PERM_DECLARED_PROGRAM_DELETE_OWN);
      }

      @Test
      void thenSelfKnowledgeManagementOwnShouldContainExactlyOwnSelfKnowledgePermissions() {
        BddLogger.then(
            "SELF_KNOWLEDGE_MANAGEMENT_OWN should contain exactly the own self-knowledge"
                + " permissions");

        assertThat(EPermissionGroup.SELF_KNOWLEDGE_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_SELF_KNOWLEDGE_LIST_OWN,
                EPermission.PERM_SELF_KNOWLEDGE_CREATE_OWN,
                EPermission.PERM_SELF_KNOWLEDGE_UPDATE_OWN,
                EPermission.PERM_SELF_KNOWLEDGE_DELETE_OWN);
      }

      @Test
      void thenNotificationManagementOwnShouldContainExactlyOwnNotificationPermissions() {
        BddLogger.then(
            "NOTIFICATION_MANAGEMENT_OWN should contain exactly the own notification permissions");

        assertThat(EPermissionGroup.NOTIFICATION_MANAGEMENT_OWN.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_NOTIFICATION_READ_OWN, EPermission.PERM_NOTIFICATION_UPDATE_OWN);
      }

      @Test
      void thenStudentActivityAccessShouldContainExactlyStudentActivityPermissions() {
        BddLogger.then(
            "STUDENT_ACTIVITY_ACCESS should contain exactly the student-facing activity"
                + " permissions");

        assertThat(EPermissionGroup.STUDENT_ACTIVITY_ACCESS.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_REGISTER_OWN,
                EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_DOWNLOAD_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL);
      }

      @Test
      void thenStudentFeedbackAccessShouldContainExactlyStudentFeedbackPermissions() {
        BddLogger.then(
            "STUDENT_FEEDBACK_ACCESS should contain exactly the student-facing feedback"
                + " permissions");

        assertThat(EPermissionGroup.STUDENT_FEEDBACK_ACCESS.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_FEEDBACK_REQUEST_CREATE_OWN,
                EPermission.PERM_FEEDBACK_RECEIVED_READ_OWN);
      }

      @Test
      void thenStaffActivityAccessShouldContainExactlyStaffActivityPermissions() {
        BddLogger.then(
            "STAFF_ACTIVITY_ACCESS should contain exactly the staff-facing activity permissions");

        assertThat(EPermissionGroup.STAFF_ACTIVITY_ACCESS.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_LIBRARY_STAFF_READ,
                EPermission.PERM_ACTIVITY_PUBLISHED_UPDATE_CONTEXTUAL,
                EPermission.PERM_ACTIVITY_DUPLICATE);
      }

      @Test
      void thenStaffFeedbackManagementShouldContainExactlyStaffFeedbackPermissions() {
        BddLogger.then(
            "STAFF_FEEDBACK_MANAGEMENT should contain exactly the staff-facing feedback"
                + " permissions");

        assertThat(EPermissionGroup.STAFF_FEEDBACK_MANAGEMENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_FEEDBACK_REQUEST_READ_ASSIGNED_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_HISTORY_READ_CONTEXTUAL,
                EPermission.PERM_FEEDBACK_DASHBOARD_READ_CONTEXTUAL);
      }

      @Test
      void thenRbacManagementShouldContainExactlyTheFourAtomicRbacPermissions() {
        BddLogger.then(
            "RBAC_MANAGEMENT should contain exactly the four atomic RBAC administration"
                + " permissions");

        assertThat(EPermissionGroup.RBAC_MANAGEMENT.permissions())
            .containsExactlyInAnyOrder(
                EPermission.PERM_RBAC_READ,
                EPermission.PERM_RBAC_ASSIGN,
                EPermission.PERM_RBAC_REVOKE,
                EPermission.PERM_RBAC_MANAGE);
      }
    }
  }
}
