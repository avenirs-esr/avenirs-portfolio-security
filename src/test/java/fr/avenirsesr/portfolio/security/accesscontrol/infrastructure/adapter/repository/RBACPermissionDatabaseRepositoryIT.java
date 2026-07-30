package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACPermissionEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACPermissionDatabaseRepositoryIT {

  @Autowired private RBACPermissionRepository permissionRepository;

  @Autowired private RBACPermissionJpaRepository permissionJpaRepository;

  @Nested
  class GivenAPermissionRepository {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC permission repository backed by seeded permissions");
    }

    @Nested
    class WhenFindingByName {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("finding a permission by name");
      }

      @Nested
      class AndThePermissionExists {
        private Optional<RBACPermission> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the permission exists");

          result = permissionRepository.findByName("PERM_READ");
        }

        @Test
        void thenItShouldReturnThePermission() {
          BddLogger.then("it should return the permission");

          assertTrue(result.isPresent());
          assertThat(result.orElseThrow().name()).isEqualTo("PERM_READ");
          assertThat(result.orElseThrow().description()).isEqualTo("Read permission");
        }
      }

      @Nested
      class AndThePermissionDoesNotExist {
        private Optional<RBACPermission> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the permission does not exist");

          result = permissionRepository.findByName("PERM_UNKNOWN");
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());
        }
      }
    }

    @Nested
    class WhenFindingAll {
      private List<RBACPermission> result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("listing all permissions");

        result = permissionRepository.findAll();
      }

      @Test
      void thenItShouldReturnAllSeededPermissions() {
        BddLogger.then("it should return all seeded permissions");

        assertThat(result)
            .extracting(RBACPermission::name)
            .containsExactlyInAnyOrder(
                "PERM_READ", "PERM_WRITE", "PERM_COMMENT", "PERM_SHARE", "PERM_DELETE");
      }
    }

    @Nested
    class WhenPersistingADuplicatePermissionName {

      @Test
      void thenItShouldViolateTheUniqueConstraint() {
        BddLogger.when("persisting a second permission with an already used name");
        BddLogger.then("it should violate the unique constraint on name");

        RBACPermissionEntity duplicate =
            new RBACPermissionEntity(null, "PERM_READ", "Duplicate read permission");

        assertThrows(
            DataIntegrityViolationException.class,
            () -> permissionJpaRepository.saveAndFlush(duplicate));
      }
    }
  }
}
