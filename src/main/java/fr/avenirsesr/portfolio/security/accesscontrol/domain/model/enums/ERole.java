package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

import java.util.HashSet;
import java.util.Set;

public enum ERole {
  ROLE_STUDENT(
      "Learner with a personal COFOLIO space for their life project and skills-based approach",
      Set.of(
          EPermission.PERM_PROFILE_READ_OWN,
          EPermission.PERM_PROFILE_UPDATE_OWN,
          EPermission.PERM_COMPETENCY_READ,
          EPermission.PERM_EMPLOYMENT_KIT_READ_OWN),
      Set.of(
          EPermissionGroup.TRACE_MANAGEMENT_OWN,
          EPermissionGroup.DECLARED_SKILL_MANAGEMENT_OWN,
          EPermissionGroup.DECLARED_EXPERIENCE_MANAGEMENT_OWN,
          EPermissionGroup.STUDENT_ACTIVITY_ACCESS,
          EPermissionGroup.STUDENT_FEEDBACK_ACCESS)),

  ROLE_STAFF(
      "Staff member providing educational guidance and handling feedback",
      Set.of(
          EPermission.PERM_PROFILE_READ_OWN,
          EPermission.PERM_PROFILE_UPDATE_OWN,
          EPermission.PERM_TRACE_READ_CONTEXTUAL,
          EPermission.PERM_TRACE_DOWNLOAD_CONTEXTUAL),
      Set.of(EPermissionGroup.STAFF_ACTIVITY_ACCESS, EPermissionGroup.STAFF_FEEDBACK_MANAGEMENT)),

  ROLE_SUPER_ADMIN(
      "Global administrator of the COFOLIO platform",
      Set.of(),
      Set.of(EPermissionGroup.ESTABLISHMENT_MANAGEMENT, EPermissionGroup.GROUP_MANAGEMENT));

  private final String description;
  private final Set<EPermission> directPermissions;
  private final Set<EPermissionGroup> permissionGroups;
  private final Set<EPermission> permissions;

  ERole(
      String description,
      Set<EPermission> directPermissions,
      Set<EPermissionGroup> permissionGroups) {
    this.description = description;
    this.directPermissions = Set.copyOf(directPermissions);
    this.permissionGroups = Set.copyOf(permissionGroups);
    this.permissions = flatten(this.directPermissions, this.permissionGroups);
  }

  public String description() {
    return description;
  }

  public Set<EPermission> permissions() {
    return permissions;
  }

  public Set<EPermission> directPermissions() {
    return directPermissions;
  }

  public Set<EPermissionGroup> permissionGroups() {
    return permissionGroups;
  }

  private static Set<EPermission> flatten(
      Set<EPermission> directPermissions, Set<EPermissionGroup> permissionGroups) {
    Set<EPermission> flattened = new HashSet<>(directPermissions);

    for (EPermissionGroup group : permissionGroups) {
      for (EPermission permission : group.permissions()) {
        if (!flattened.add(permission)) {
          throw new IllegalStateException(
              "Permission "
                  + permission
                  + " is declared both directly and via group "
                  + group
                  + "; remove the redundant declaration.");
        }
      }
    }

    return Set.copyOf(flattened);
  }
}
