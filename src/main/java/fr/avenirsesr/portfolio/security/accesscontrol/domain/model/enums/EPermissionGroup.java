package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import java.util.Set;

public enum EPermissionGroup {
  ESTABLISHMENT_MANAGEMENT(
      Set.of(
          EPermission.PERM_PRIMARY_ESTABLISHMENT_READ,
          EPermission.PERM_PRIMARY_ESTABLISHMENT_CREATE,
          EPermission.PERM_PRIMARY_ESTABLISHMENT_UPDATE,
          EPermission.PERM_PRIMARY_ESTABLISHMENT_DELETE,
          EPermission.PERM_SECONDARY_ESTABLISHMENT_READ,
          EPermission.PERM_SECONDARY_ESTABLISHMENT_CREATE,
          EPermission.PERM_SECONDARY_ESTABLISHMENT_UPDATE,
          EPermission.PERM_SECONDARY_ESTABLISHMENT_DELETE)),

  GROUP_MANAGEMENT(
      Set.of(
          EPermission.PERM_GROUP_READ,
          EPermission.PERM_GROUP_IMPORT,
          EPermission.PERM_GROUP_CREATE,
          EPermission.PERM_GROUP_UPDATE,
          EPermission.PERM_GROUP_DELETE)),

  TRACE_MANAGEMENT_OWN(
      Set.of(
          EPermission.PERM_TRACE_CREATE_OWN,
          EPermission.PERM_TRACE_LIST_OWN,
          EPermission.PERM_TRACE_ASSOCIATION_MANAGE_OWN)),

  DECLARED_SKILL_MANAGEMENT_OWN(
      Set.of(
          EPermission.PERM_DECLARED_SKILL_LIST_OWN,
          EPermission.PERM_DECLARED_SKILL_CREATE_OWN,
          EPermission.PERM_DECLARED_SKILL_UPDATE_OWN,
          EPermission.PERM_DECLARED_SKILL_DELETE_OWN,
          EPermission.PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN)),

  DECLARED_EXPERIENCE_MANAGEMENT_OWN(
      Set.of(
          EPermission.PERM_DECLARED_EXPERIENCE_LIST_OWN,
          EPermission.PERM_DECLARED_EXPERIENCE_CREATE_OWN,
          EPermission.PERM_DECLARED_EXPERIENCE_DELETE_OWN,
          EPermission.PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN)),

  STUDENT_ACTIVITY_ACCESS(
      Set.of(
          EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_REGISTER_OWN,
          EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_DOCUMENT_DOWNLOAD_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL)),

  STUDENT_FEEDBACK_ACCESS(
      Set.of(
          EPermission.PERM_FEEDBACK_REQUEST_CREATE_OWN,
          EPermission.PERM_FEEDBACK_RECEIVED_READ_OWN)),

  STAFF_ACTIVITY_ACCESS(
      Set.of(
          EPermission.PERM_ACTIVITY_CATALOG_READ_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_READ_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_DOCUMENT_READ_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_LIBRARY_STAFF_READ,
          EPermission.PERM_ACTIVITY_PUBLISHED_UPDATE_CONTEXTUAL)),

  STAFF_FEEDBACK_MANAGEMENT(
      Set.of(
          EPermission.PERM_FEEDBACK_REQUEST_READ_ASSIGNED_CONTEXTUAL,
          EPermission.PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED_CONTEXTUAL,
          EPermission.PERM_FEEDBACK_HISTORY_READ_CONTEXTUAL,
          EPermission.PERM_FEEDBACK_DASHBOARD_READ_CONTEXTUAL)),

  RBAC_MANAGEMENT(
      Set.of(
          EPermission.PERM_RBAC_READ,
          EPermission.PERM_RBAC_ASSIGN,
          EPermission.PERM_RBAC_REVOKE,
          EPermission.PERM_RBAC_MANAGE));

  private final Set<EPermission> permissions;

  EPermissionGroup(Set<EPermission> permissions) {
    if (permissions.isEmpty()) {
      throw new IllegalStateException("Permission group " + name() + " must not be empty");
    }
    this.permissions = Set.copyOf(permissions);
  }

  public Set<EPermission> permissions() {
    return permissions;
  }
}
