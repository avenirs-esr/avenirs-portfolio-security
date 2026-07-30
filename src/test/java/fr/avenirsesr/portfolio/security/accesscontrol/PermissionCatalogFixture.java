package fr.avenirsesr.portfolio.security.accesscontrol;

import java.util.List;
import java.util.UUID;

/**
 * Test-only mirror of the permission reference data inserted by the Liquibase changelogs under
 * {@code db/changelog/data}. Centralizing the expected catalog here (rather than in production
 * code) lets migration/repository tests assert on the exact expected set without duplicating it
 * across test classes.
 */
public final class PermissionCatalogFixture {

  public record ExpectedPermission(UUID id, String name, String description) {}

  public static final List<ExpectedPermission> ALL =
      List.of(
          // profile
          entry(
              "a1000000-0000-0000-0000-000000000001",
              "PERM_PROFILE_READ_OWN",
              "Consulter son propre profil"),
          entry(
              "a1000000-0000-0000-0000-000000000002",
              "PERM_PROFILE_UPDATE_OWN",
              "Modifier son propre profil ou sa photo"),
          // trace
          entry(
              "a2000000-0000-0000-0000-000000000001",
              "PERM_TRACE_CREATE_OWN",
              "Ajouter une trace dans son espace personnel"),
          entry(
              "a2000000-0000-0000-0000-000000000002",
              "PERM_TRACE_LIST_OWN",
              "Consulter la liste de ses propres traces"),
          entry(
              "a2000000-0000-0000-0000-000000000003",
              "PERM_TRACE_READ_CONTEXTUAL",
              "Consulter une trace rendue accessible dans un contexte autorisé"),
          entry(
              "a2000000-0000-0000-0000-000000000004",
              "PERM_TRACE_DOWNLOAD_CONTEXTUAL",
              "Télécharger une trace rendue accessible dans un contexte autorisé"),
          entry(
              "a2000000-0000-0000-0000-000000000005",
              "PERM_TRACE_ASSOCIATION_MANAGE_OWN",
              "Associer une trace à une compétence, une activité ou une expérience"),
          // competency
          entry(
              "a3000000-0000-0000-0000-000000000001",
              "PERM_COMPETENCY_READ",
              "Consulter les compétences visées du référentiel"),
          // declared skill
          entry(
              "a4000000-0000-0000-0000-000000000001",
              "PERM_DECLARED_SKILL_LIST_OWN",
              "Consulter la liste de ses compétences déclarées"),
          entry(
              "a4000000-0000-0000-0000-000000000002",
              "PERM_DECLARED_SKILL_CREATE_OWN",
              "Ajouter une compétence déclarée"),
          entry(
              "a4000000-0000-0000-0000-000000000003",
              "PERM_DECLARED_SKILL_UPDATE_OWN",
              "Modifier une compétence déclarée"),
          entry(
              "a4000000-0000-0000-0000-000000000004",
              "PERM_DECLARED_SKILL_DELETE_OWN",
              "Supprimer une compétence déclarée"),
          entry(
              "a4000000-0000-0000-0000-000000000005",
              "PERM_DECLARED_SKILL_ASSOCIATION_MANAGE_OWN",
              "Associer ou supprimer des associations d'une compétence déclarée"),
          // declared experience
          entry(
              "a5000000-0000-0000-0000-000000000001",
              "PERM_DECLARED_EXPERIENCE_LIST_OWN",
              "Consulter la liste de ses expériences déclarées"),
          entry(
              "a5000000-0000-0000-0000-000000000002",
              "PERM_DECLARED_EXPERIENCE_CREATE_OWN",
              "Ajouter une expérience déclarée"),
          entry(
              "a5000000-0000-0000-0000-000000000003",
              "PERM_DECLARED_EXPERIENCE_DELETE_OWN",
              "Supprimer une expérience déclarée"),
          entry(
              "a5000000-0000-0000-0000-000000000004",
              "PERM_DECLARED_EXPERIENCE_ASSOCIATION_MANAGE_OWN",
              "Associer une trace ou une compétence à une expérience déclarée"),
          // activity
          entry(
              "a6000000-0000-0000-0000-000000000001",
              "PERM_ACTIVITY_CATALOG_READ",
              "Consulter le catalogue des activités"),
          entry(
              "a6000000-0000-0000-0000-000000000002",
              "PERM_ACTIVITY_REGISTER_OWN",
              "S'inscrire à une activité"),
          entry(
              "a6000000-0000-0000-0000-000000000003",
              "PERM_ACTIVITY_READ",
              "Consulter le détail d'une activité"),
          entry(
              "a6000000-0000-0000-0000-000000000004",
              "PERM_ACTIVITY_DOCUMENT_READ",
              "Consulter les documents et liens d'une activité"),
          entry(
              "a6000000-0000-0000-0000-000000000005",
              "PERM_EMPLOYMENT_KIT_READ_OWN",
              "Consulter la page Mon kit prêt à l'emploi"),
          entry(
              "a6000000-0000-0000-0000-000000000006",
              "PERM_ACTIVITY_LIBRARY_STAFF_READ",
              "Consulter la bibliothèque d'activités destinée au personnel"),
          entry(
              "a6000000-0000-0000-0000-000000000007",
              "PERM_ACTIVITY_NATIONAL_CREATE",
              "Créer une activité nationale"),
          entry(
              "a6000000-0000-0000-0000-000000000008",
              "PERM_ACTIVITY_NATIONAL_UPDATE",
              "Renseigner ou modifier le contenu d'une activité nationale non publiée"),
          entry(
              "a6000000-0000-0000-0000-000000000009",
              "PERM_ACTIVITY_PUBLISHED_UPDATE",
              "Modifier une activité nationale publiée"),
          entry(
              "a6000000-0000-0000-0000-000000000010",
              "PERM_ACTIVITY_NATIONAL_DELETE",
              "Supprimer une activité nationale non publiée"),
          entry(
              "a6000000-0000-0000-0000-000000000011",
              "PERM_ACTIVITY_FEEDBACK_SETTINGS_UPDATE",
              "Paramétrer la demande de feedback d'une activité nationale"),
          // feedback
          entry(
              "a7000000-0000-0000-0000-000000000001",
              "PERM_FEEDBACK_REQUEST_CREATE_OWN",
              "Faire une demande de feedback"),
          entry(
              "a7000000-0000-0000-0000-000000000002",
              "PERM_FEEDBACK_RECEIVED_READ_OWN",
              "Consulter les feedbacks reçus"),
          entry(
              "a7000000-0000-0000-0000-000000000003",
              "PERM_FEEDBACK_REQUEST_READ_ASSIGNED",
              "Consulter les demandes de feedback qui lui sont assignées"),
          entry(
              "a7000000-0000-0000-0000-000000000004",
              "PERM_FEEDBACK_REQUEST_RESPOND_ASSIGNED",
              "Répondre à une demande de feedback assignée"),
          entry(
              "a7000000-0000-0000-0000-000000000005",
              "PERM_FEEDBACK_HISTORY_READ",
              "Consulter l'historique des feedbacks de son établissement"),
          entry(
              "a7000000-0000-0000-0000-000000000006",
              "PERM_FEEDBACK_DASHBOARD_READ",
              "Consulter le tableau de bord des feedbacks de son établissement"),
          // administration - primary establishment
          entry(
              "a8000000-0000-0000-0000-000000000001",
              "PERM_PRIMARY_ESTABLISHMENT_READ",
              "Consulter un établissement principal"),
          entry(
              "a8000000-0000-0000-0000-000000000002",
              "PERM_PRIMARY_ESTABLISHMENT_CREATE",
              "Créer un établissement principal"),
          entry(
              "a8000000-0000-0000-0000-000000000003",
              "PERM_PRIMARY_ESTABLISHMENT_UPDATE",
              "Modifier un établissement principal"),
          entry(
              "a8000000-0000-0000-0000-000000000004",
              "PERM_PRIMARY_ESTABLISHMENT_DELETE",
              "Supprimer un établissement principal"),
          // administration - secondary establishment
          entry(
              "a8000000-0000-0000-0000-000000000005",
              "PERM_SECONDARY_ESTABLISHMENT_READ",
              "Consulter un établissement secondaire"),
          entry(
              "a8000000-0000-0000-0000-000000000006",
              "PERM_SECONDARY_ESTABLISHMENT_CREATE",
              "Créer un établissement secondaire"),
          entry(
              "a8000000-0000-0000-0000-000000000007",
              "PERM_SECONDARY_ESTABLISHMENT_UPDATE",
              "Modifier un établissement secondaire"),
          entry(
              "a8000000-0000-0000-0000-000000000008",
              "PERM_SECONDARY_ESTABLISHMENT_DELETE",
              "Supprimer un établissement secondaire"),
          // administration - group
          entry("a8000000-0000-0000-0000-000000000009", "PERM_GROUP_READ", "Consulter un groupe"),
          entry(
              "a8000000-0000-0000-0000-000000000010",
              "PERM_GROUP_IMPORT",
              "Alimenter un groupe par import"),
          entry("a8000000-0000-0000-0000-000000000011", "PERM_GROUP_CREATE", "Créer un groupe"),
          entry("a8000000-0000-0000-0000-000000000012", "PERM_GROUP_UPDATE", "Modifier un groupe"),
          entry(
              "a8000000-0000-0000-0000-000000000013", "PERM_GROUP_DELETE", "Supprimer un groupe"));

  private static ExpectedPermission entry(String id, String name, String description) {
    return new ExpectedPermission(UUID.fromString(id), name, description);
  }

  private PermissionCatalogFixture() {}
}
