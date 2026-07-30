package fr.avenirsesr.portfolio.security.accesscontrol.migration;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.security.accesscontrol.PermissionCatalogFixture;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Boots the application against a dedicated, empty H2 database with Liquibase actually enabled
 * (unlike the default {@code test} profile, which disables Liquibase and lets Hibernate create the
 * schema). This is the only way, without introducing Testcontainers, to verify that the real
 * changelogs under {@code db/changelog/data} apply cleanly on a vierge database and produce exactly
 * the expected permission catalog.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:permission_catalog_migration_it;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
      "spring.liquibase.enabled=true",
      // The real master changelog also runs db/changelog/generated, which declares an
      // unrelated column (action_route.method) with the PostgreSQL-only "citext" type that H2
      // cannot create. This test therefore points Liquibase at a test-only changelog that
      // provisions just the "permission" table and then runs the REAL, unmodified changesets
      // under db/changelog/data - the actual deliverable of this task.
      "spring.liquibase.change-log=classpath:db/changelog/data-only-test-changelog.xml",
      // application.properties pins the Liquibase schema to "dev" for real Postgres
      // deployments; the isolated H2 test database only has the default schema.
      "spring.liquibase.default-schema=",
      "spring.jpa.hibernate.ddl-auto=none"
    })
class RBACPermissionCatalogMigrationIT {

  @Autowired private RBACPermissionRepository permissionRepository;

  @Nested
  class GivenAViergeDatabaseMigratedWithLiquibase {

    private List<RBACPermission> persistedPermissions;

    @BeforeEach
    void setupGiven() {
      persistedPermissions = permissionRepository.findAll();
    }

    @Test
    void thenItShouldContainExactlyTheExpectedPermissionNames() {
      assertThat(persistedPermissions)
          .extracting(RBACPermission::name)
          .containsExactlyInAnyOrderElementsOf(
              PermissionCatalogFixture.ALL.stream()
                  .map(PermissionCatalogFixture.ExpectedPermission::name)
                  .toList());
    }

    @Test
    void thenEveryPermissionShouldMatchItsExpectedIdAndDescription() {
      Map<String, RBACPermission> persistedByName =
          persistedPermissions.stream().collect(toMap(RBACPermission::name, identity()));

      for (PermissionCatalogFixture.ExpectedPermission expected : PermissionCatalogFixture.ALL) {
        RBACPermission actual = persistedByName.get(expected.name());

        assertThat(actual).as("permission %s should be present", expected.name()).isNotNull();
        assertThat(actual.id()).as("id of %s", expected.name()).isEqualTo(expected.id());
        assertThat(actual.description())
            .as("description of %s", expected.name())
            .isEqualTo(expected.description());
      }
    }

    @Test
    void thenAllPermissionNamesShouldBeUnique() {
      assertThat(persistedPermissions).extracting(RBACPermission::name).doesNotHaveDuplicates();
    }

    @Test
    void thenAllPermissionIdsShouldBeUnique() {
      assertThat(persistedPermissions).extracting(RBACPermission::id).doesNotHaveDuplicates();
    }

    @Test
    void thenAllDescriptionsShouldBeNonBlank() {
      assertThat(persistedPermissions)
          .allSatisfy(permission -> assertThat(permission.description()).isNotBlank());
    }

    @Test
    void thenNoUnexpectedPermissionShouldHaveBeenInserted() {
      assertThat(persistedPermissions).hasSize(PermissionCatalogFixture.ALL.size());
    }
  }
}
