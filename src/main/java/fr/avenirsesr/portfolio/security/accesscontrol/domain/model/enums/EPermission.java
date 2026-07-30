package fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums;

public enum EPermission {
  PERM_PROFILE_READ_OWN("View own profile"),
  PERM_PROFILE_UPDATE_OWN("Update own profile or photo"),

  PERM_TRACE_CREATE_OWN("Add a trace to own personal space"),
  PERM_TRACE_LIST_OWN("View the list of own traces"),
  PERM_TRACE_READ_CONTEXTUAL("View a trace made accessible in an authorized context"),
  PERM_TRACE_DOWNLOAD_CONTEXTUAL("Download a trace made accessible in an authorized context"),
  PERM_TRACE_ASSOCIATION_MANAGE_OWN(
      "Associate a trace with a competency, an activity or an experience"),

  PERM_COMPETENCY_READ("View the target competencies of the framework"),

  PERM_DECLARED_SKILL_LIST_OWN("View the list of own declared skills"),
  PERM_DECLARED_SKILL_CREATE_OWN("Add a declared skill"),
  PERM_DECLARED_SKILL_UPDATE_OWN("Update a declared skill"),
  PERM_DECLARED_SKILL_DELETE_OWN("Delete a declared skill"),
  PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN("Add or remove associations of a declared skill"),

  PERM_DECLARED_EXPERIENCE_LIST_OWN("View the list of own declared experiences"),
  PERM_DECLARED_EXPERIENCE_CREATE_OWN("Add a declared experience"),
  PERM_DECLARED_EXPERIENCE_DELETE_OWN("Delete a declared experience"),
  PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN(
      "Associate a trace or a competency with a declared experience"),

  PERM_ACTIVITY_CATALOG_READ("View the activity catalog"),
  PERM_ACTIVITY_REGISTER_OWN("Register for an activity"),
  PERM_ACTIVITY_READ("View activity details"),
  PERM_ACTIVITY_DOCUMENT_READ("View an activity's documents and links"),
  PERM_EMPLOYMENT_KIT_READ_OWN("View the My employment kit page"),
  PERM_ACTIVITY_LIBRARY_STAFF_READ("View the staff activity library"),
  PERM_ACTIVITY_NATIONAL_CREATE("Create a national activity"),
  PERM_ACTIVITY_NATIONAL_UPDATE("Update the content of an unpublished national activity"),
  PERM_ACTIVITY_PUBLISHED_UPDATE("Update a published national activity"),
  PERM_ACTIVITY_NATIONAL_DELETE("Delete an unpublished national activity"),
  PERM_ACTIVITY_FEEDBACK_SETTINGS_UPDATE(
      "Configure the feedback request settings of a national activity"),

  PERM_FEEDBACK_REQUEST_CREATE_OWN("Request feedback"),
  PERM_FEEDBACK_RECEIVED_READ_OWN("View received feedback"),
  PERM_FEEDBACK_REQUEST_READ_ASSIGNED("View feedback requests assigned to them"),
  PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED("Respond to an assigned feedback request"),
  PERM_FEEDBACK_HISTORY_READ("View the feedback history of their institution"),
  PERM_FEEDBACK_DASHBOARD_READ("View the feedback dashboard of their institution"),

  PERM_PRIMARY_ESTABLISHMENT_READ("View a primary institution"),
  PERM_PRIMARY_ESTABLISHMENT_CREATE("Create a primary institution"),
  PERM_PRIMARY_ESTABLISHMENT_UPDATE("Update a primary institution"),
  PERM_PRIMARY_ESTABLISHMENT_DELETE("Delete a primary institution"),
  PERM_SECONDARY_ESTABLISHMENT_READ("View a secondary institution"),
  PERM_SECONDARY_ESTABLISHMENT_CREATE("Create a secondary institution"),
  PERM_SECONDARY_ESTABLISHMENT_UPDATE("Update a secondary institution"),
  PERM_SECONDARY_ESTABLISHMENT_DELETE("Delete a secondary institution"),
  PERM_GROUP_READ("View a group"),
  PERM_GROUP_IMPORT("Populate a group via import"),
  PERM_GROUP_CREATE("Create a group"),
  PERM_GROUP_UPDATE("Update a group"),
  PERM_GROUP_DELETE("Delete a group");

  private final String description;

  EPermission(String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }
}
