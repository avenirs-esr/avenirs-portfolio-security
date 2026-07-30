package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import java.util.Set;

public enum ERole {
  ROLE_STUDENT(
      "Learner with a personal COFOLIO space for their life project and skills-based approach",
      Set.of(
          EPermission.PERM_PROFILE_READ_OWN,
          EPermission.PERM_PROFILE_UPDATE_OWN,
          EPermission.PERM_TRACE_CREATE_OWN,
          EPermission.PERM_TRACE_LIST_OWN,
          EPermission.PERM_TRACE_READ_CONTEXTUAL,
          EPermission.PERM_TRACE_DOWNLOAD_CONTEXTUAL,
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
          EPermission.PERM_ACTIVITY_CATALOG_READ,
          EPermission.PERM_ACTIVITY_REGISTER_OWN,
          EPermission.PERM_ACTIVITY_READ,
          EPermission.PERM_ACTIVITY_DOCUMENT_READ,
          EPermission.PERM_EMPLOYMENT_KIT_READ_OWN,
          EPermission.PERM_FEEDBACK_REQUEST_CREATE_OWN,
          EPermission.PERM_FEEDBACK_RECEIVED_READ_OWN)),

  ROLE_STAFF(
      "Staff member providing educational guidance and handling feedback",
      Set.of(
          EPermission.PERM_PROFILE_READ_OWN,
          EPermission.PERM_PROFILE_UPDATE_OWN,
          EPermission.PERM_TRACE_READ_CONTEXTUAL,
          EPermission.PERM_TRACE_DOWNLOAD_CONTEXTUAL,
          EPermission.PERM_ACTIVITY_CATALOG_READ,
          EPermission.PERM_ACTIVITY_READ,
          EPermission.PERM_ACTIVITY_DOCUMENT_READ,
          EPermission.PERM_ACTIVITY_LIBRARY_STAFF_READ,
          EPermission.PERM_ACTIVITY_PUBLISHED_UPDATE,
          EPermission.PERM_FEEDBACK_REQUEST_READ_ASSIGNED,
          EPermission.PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED,
          EPermission.PERM_FEEDBACK_HISTORY_READ,
          EPermission.PERM_FEEDBACK_DASHBOARD_READ)),

  ROLE_SUPER_ADMIN(
      "Global administrator of the COFOLIO platform",
      Set.of(
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
          EPermission.PERM_GROUP_DELETE));

  private final String description;
  private final Set<EPermission> permissions;

  ERole(String description, Set<EPermission> permissions) {
    this.description = description;
    this.permissions = permissions;
  }

  public String description() {
    return description;
  }

  public Set<EPermission> permissions() {
    return permissions;
  }
}
