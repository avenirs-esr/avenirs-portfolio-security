package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.client.ExternalUserClient;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository.PrincipalJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PrincipalSeederTest {

  @Mock private ExternalUserClient externalUserClient;
  @Mock private PrincipalJpaRepository principalJpaRepository;

  private PrincipalSeeder seeder;

  @BeforeEach
  void setup() {
    seeder = new PrincipalSeeder(externalUserClient, principalJpaRepository);
    ReflectionTestUtils.setField(seeder, "defaultPassword", "default");
    ReflectionTestUtils.setField(seeder, "amuDefaultPassword", "amu");
    ReflectionTestUtils.setField(seeder, "uboDefaultPassword", "ubo");
    ReflectionTestUtils.setField(seeder, "usmbDefaultPassword", "usmb");
    ReflectionTestUtils.setField(seeder, "ulilleDefaultPassword", "ulille");
    ReflectionTestUtils.setField(seeder, "rpltkDefaultPassword", "rpltk");
    ReflectionTestUtils.setField(seeder, "utlnDefaultPassword", "utln");
    ReflectionTestUtils.setField(seeder, "nuDefaultPassword", "nu");
    ReflectionTestUtils.setField(seeder, "anrDefaultPassword", "anr");
    ReflectionTestUtils.setField(seeder, "iaUsmbDefaultPassword", "ia-usmb");
  }

  private ExternalUserDTO externalUser(String eppn) {
    return new ExternalUserDTO(
        eppn,
        "first",
        "last",
        eppn,
        Set.of(EUserCategory.STUDENT),
        "ext-" + eppn,
        "OIDC",
        List.of(),
        List.of(),
        EUserStatus.ACTIVE);
  }

  private PrincipalEntity existingPrincipal(String login) {
    PrincipalEntity entity = new PrincipalEntity();
    entity.setLogin(login);
    return entity;
  }

  @Nested
  class GivenNoPrincipalInDatabase {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("external users and an empty principal table");

      when(externalUserClient.getAll())
          .thenReturn(List.of(externalUser("a@univ.fr"), externalUser("b@univ.fr")));
      when(principalJpaRepository.findByLogin("a@univ.fr")).thenReturn(Optional.empty());
      when(principalJpaRepository.findByLogin("b@univ.fr")).thenReturn(Optional.empty());
      when(principalJpaRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void thenAllExternalUsersShouldBeCreated() {
      BddLogger.when("seeding principals");
      BddLogger.then("every external user should be saved as a principal");

      List<PrincipalEntity> saved = seeder.seed();

      assertThat(saved)
          .extracting(PrincipalEntity::getLogin)
          .containsExactly("a@univ.fr", "b@univ.fr");
    }
  }

  @Nested
  class GivenSomePrincipalsAlreadyInDatabase {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("one external user already persisted and one new");

      when(externalUserClient.getAll())
          .thenReturn(List.of(externalUser("existing@univ.fr"), externalUser("new@univ.fr")));
      when(principalJpaRepository.findByLogin("existing@univ.fr"))
          .thenReturn(Optional.of(existingPrincipal("existing@univ.fr")));
      when(principalJpaRepository.findByLogin("new@univ.fr")).thenReturn(Optional.empty());
      when(principalJpaRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void thenOnlyTheMissingPrincipalShouldBeCreated() {
      BddLogger.when("seeding principals");
      BddLogger.then("the existing principal should be left untouched and only the new one saved");

      List<PrincipalEntity> saved = seeder.seed();

      assertThat(saved).extracting(PrincipalEntity::getLogin).containsExactly("new@univ.fr");
    }
  }

  @Nested
  class GivenAllPrincipalsAlreadyInDatabase {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("all external users already persisted");

      when(externalUserClient.getAll()).thenReturn(List.of(externalUser("existing@univ.fr")));
      when(principalJpaRepository.findByLogin("existing@univ.fr"))
          .thenReturn(Optional.of(existingPrincipal("existing@univ.fr")));
      when(principalJpaRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
          .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void thenNothingShouldBeCreated() {
      BddLogger.when("seeding principals again");
      BddLogger.then("no principal should be saved");

      assertThat(seeder.seed()).isEmpty();
    }
  }
}
