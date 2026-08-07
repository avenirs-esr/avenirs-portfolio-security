package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.RBACCatalogSynchronizationException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACPermissionEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACRoleEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository.RBACActionJpaRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository.RBACPermissionJpaRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository.RBACRoleJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Covers the parts of {@link RBACCatalogSeeder} that {@code RBACCatalogSeederIT} (a full
 * empty-database run) does not exercise: migrating rows already persisted under their legacy PERM_*
 * name, refusing to reconcile an ambiguous catalog, and keeping a managed role's relations exactly
 * in sync (adding missing ones, removing obsolete ones without touching the permission row itself).
 *
 * <p>{@code SeederRunner} now synchronizes the catalog unconditionally on every application startup
 * (see {@code SeederOrchestrator#synchronizeCatalog}), so by the time any test method here runs,
 * the shared Spring context has already fully migrated the catalog once. Each test wipes the
 * catalog tables first to reconstruct a genuine pre-migration (or conflicting) state independently
 * of that startup run.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RBACCatalogSeederMigrationIT {

  @Autowired private RBACCatalogSeeder rbacCatalogSeeder;
  @Autowired private RBACPermissionJpaRepository permissionJpaRepository;
  @Autowired private RBACRoleJpaRepository roleJpaRepository;
  @Autowired private RBACActionJpaRepository actionJpaRepository;
  @Autowired private RBACPermissionRepository permissionRepository;
  @Autowired private RBACRoleRepository roleRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void resetCatalog() {
    jdbcTemplate.execute("DELETE FROM action_permission");
    jdbcTemplate.execute("DELETE FROM role_permission");
    jdbcTemplate.execute("DELETE FROM action");
    jdbcTemplate.execute("DELETE FROM permission");
    jdbcTemplate.execute("DELETE FROM role");
  }

  @Nested
  class GivenAPermissionRowPersistedUnderItsLegacyName {
    private UUID legacyId;

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a permission row persisted under its legacy PERM_* name");

      RBACPermissionEntity legacy =
          permissionJpaRepository.save(
              new RBACPermissionEntity(
                  null, "PERM_TRACE_CREATE_OWN", "Add a trace to own personal space"));
      legacyId = legacy.getId();

      RBACActionEntity action = new RBACActionEntity();
      action.setName("ACT_TEST_TRACE_CREATE");
      action.setDescription("Test action referencing the legacy permission row");
      action.getPermissions().add(legacy);
      actionJpaRepository.save(action);
    }

    @Nested
    class WhenSynchronizing {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("synchronizing the RBAC catalog");

        rbacCatalogSeeder.seed();
      }

      @Test
      void thenTheRowIsRenamedInPlacePreservingItsIdAndRelations() {
        BddLogger.then(
            "the row keeps its id, is renamed to the new authority, and its role_permission /"
                + " action_permission relations survive");

        var renamed =
            permissionRepository.findByName(EPermission.PERM_TRACE_CREATE_OWN.authority());
        assertThat(renamed).isPresent();
        assertThat(renamed.orElseThrow().id()).isEqualTo(legacyId);

        assertThat(permissionRepository.findByName("PERM_TRACE_CREATE_OWN")).isEmpty();

        RBACActionEntity reloadedAction =
            actionJpaRepository.findByName("ACT_TEST_TRACE_CREATE").orElseThrow();
        assertThat(reloadedAction.getPermissions())
            .extracting(RBACPermissionEntity::getId)
            .contains(legacyId);

        RBACRole studentRole = roleRepository.findByName("ROLE_STUDENT").orElseThrow();
        assertThat(studentRole.permissions()).extracting(RBACPermission::id).contains(legacyId);
      }
    }
  }

  @Nested
  class GivenBothLegacyAndNewAuthorityRowsExist {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("both the legacy name and the new authority already exist as two rows");

      permissionJpaRepository.save(
          new RBACPermissionEntity(
              null, "PERM_TRACE_CREATE_OWN", "Add a trace to own personal space"));
      permissionJpaRepository.save(
          new RBACPermissionEntity(null, "trace:create:own", "A different description"));
    }

    @Nested
    class WhenSynchronizing {

      @Test
      void thenItShouldFailExplicitlyInsteadOfMergingSilently() {
        BddLogger.when("synchronizing the RBAC catalog");
        BddLogger.then("it should fail explicitly instead of merging the two rows silently");

        assertThrows(RBACCatalogSynchronizationException.class, rbacCatalogSeeder::seed);
      }
    }
  }

  @Nested
  class GivenTheCatalogAlreadySynchronizedOnce {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the RBAC catalog already synchronized once");

      rbacCatalogSeeder.seed();
    }

    @Nested
    class WhenAnUnrelatedPermissionIsManuallyLinkedToAManagedRole {
      private UUID obsoletePermissionId;

      @BeforeEach
      void setupWhen() {
        BddLogger.when(
            "an unrelated permission is linked to ROLE_STUDENT and the catalog is"
                + " resynchronized");

        RBACPermissionEntity obsolete =
            permissionJpaRepository.save(
                new RBACPermissionEntity(null, "obsolete:test:permission", "Obsolete permission"));
        obsoletePermissionId = obsolete.getId();

        RBACRoleEntity studentRole = roleJpaRepository.findByName("ROLE_STUDENT").orElseThrow();
        studentRole.getPermissions().add(obsolete);
        roleJpaRepository.save(studentRole);

        rbacCatalogSeeder.seed();
      }

      @Test
      void thenTheObsoleteRelationIsRemovedButThePermissionRowRemains() {
        BddLogger.then(
            "the obsolete role_permission relation is removed, but the permission row itself"
                + " remains");

        RBACRole studentRole = roleRepository.findByName("ROLE_STUDENT").orElseThrow();
        assertThat(studentRole.permissions())
            .extracting(RBACPermission::id)
            .doesNotContain(obsoletePermissionId);

        assertThat(permissionRepository.findByName("obsolete:test:permission")).isPresent();
      }
    }

    @Nested
    class WhenAnExpectedRelationIsManuallyRemovedFromAManagedRole {

      @BeforeEach
      void setupWhen() {
        BddLogger.when(
            "an expected relation is removed from ROLE_STUDENT and the catalog is"
                + " resynchronized");

        RBACRoleEntity studentRole = roleJpaRepository.findByName("ROLE_STUDENT").orElseThrow();
        studentRole
            .getPermissions()
            .removeIf(p -> p.getName().equals(EPermission.PERM_TRACE_CREATE_OWN.authority()));
        roleJpaRepository.save(studentRole);

        rbacCatalogSeeder.seed();
      }

      @Test
      void thenTheMissingRelationIsAddedBack() {
        BddLogger.then("the missing relation is added back");

        RBACRole studentRole = roleRepository.findByName("ROLE_STUDENT").orElseThrow();
        assertThat(studentRole.permissions())
            .extracting(RBACPermission::name)
            .contains(EPermission.PERM_TRACE_CREATE_OWN.authority());
      }
    }
  }
}
