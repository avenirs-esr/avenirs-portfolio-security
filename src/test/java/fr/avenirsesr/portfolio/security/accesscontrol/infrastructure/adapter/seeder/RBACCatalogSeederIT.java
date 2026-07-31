package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RBACCatalogSeederIT {

  @Autowired private RBACCatalogSeeder rbacCatalogSeeder;

  @Autowired private RBACRoleRepository roleRepository;

  @Autowired private RBACPermissionRepository permissionRepository;

  @Nested
  class GivenTheRbacCatalogSeeder {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("the RBAC catalog seeder backed by the ERole/EPermission enums");

      rbacCatalogSeeder.seed();
    }

    @Nested
    class WhenSeedingCompletes {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("seeding completes");
      }

      @ParameterizedTest
      @EnumSource(EPermission.class)
      void thenEveryCatalogPermissionShouldExistInDatabase(EPermission permission) {
        BddLogger.then("every EPermission should exist in the permission table");

        Optional<RBACPermission> stored = permissionRepository.findByName(permission.name());

        assertThat(stored).isPresent();
        assertThat(stored.orElseThrow().description()).isEqualTo(permission.description());
      }

      @ParameterizedTest
      @EnumSource(ERole.class)
      void thenEveryRoleShouldHaveExactlyItsFlattenedPermissionsInDatabase(ERole role) {
        BddLogger.then(
            "every ERole should be persisted with exactly the permissions returned by"
                + " permissions()");

        Optional<RBACRole> stored = roleRepository.findByName(role.name());

        assertThat(stored).isPresent();

        Set<String> storedPermissionNames =
            stored.orElseThrow().permissions().stream()
                .map(RBACPermission::name)
                .collect(Collectors.toSet());

        Set<String> expectedPermissionNames =
            role.permissions().stream().map(EPermission::name).collect(Collectors.toSet());

        assertThat(storedPermissionNames)
            .containsExactlyInAnyOrderElementsOf(expectedPermissionNames);
      }
    }
  }
}
