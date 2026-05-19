package fr.avenirsesr.portfolio.security.principal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrincipalServiceImplTest {

  private static final UUID PRINCIPAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
  private static final UUID STRUCTURE_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");

  private static final String LOGIN = "gribonvald";
  private static final String UNKNOWN_LOGIN = "unknown";

  private static final String PROVIDER = "OIDC";
  private static final String EXTERNAL_ID = "gribonvald";
  private static final String UNKNOWN_EXTERNAL_ID = "unknown-external-id";

  @Mock private PrincipalRepository principalRepository;

  private PrincipalServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PrincipalServiceImpl(principalRepository);
  }

  @Nested
  class GivenPrincipalService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a principal service");
    }

    @Nested
    class WhenGettingAllPrincipals {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all principals");
      }

      @Nested
      class AndTheRepositoryContainsPrincipals {
        private Principal principal;
        private List<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains principals");

          principal = principal();

          when(principalRepository.findAll()).thenReturn(List.of(principal));

          result = service.getAllPrincipals();
        }

        @Test
        void thenItShouldReturnRepositoryPrincipals() {
          BddLogger.then("it should return repository principals");

          assertThat(result).containsExactly(principal);

          verify(principalRepository).findAll();
          verifyNoMoreInteractions(principalRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(principalRepository.findAll()).thenReturn(List.of());

          result = service.getAllPrincipals();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(principalRepository).findAll();
          verifyNoMoreInteractions(principalRepository);
        }
      }
    }

    @Nested
    class WhenGettingPrincipalByLogin {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting principal by login");
      }

      @Nested
      class AndThePrincipalExists {
        private Principal principal;
        private Optional<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal exists");

          principal = principal();

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));

          result = service.getPrincipalByLogin(LOGIN);
        }

        @Test
        void thenItShouldReturnPrincipal() {
          BddLogger.then("it should return principal");

          assertTrue(result.isPresent());
          assertEquals(principal, result.orElseThrow());

          verify(principalRepository).findByLogin(LOGIN);
          verifyNoMoreInteractions(principalRepository);
        }
      }

      @Nested
      class AndThePrincipalDoesNotExist {
        private Optional<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal does not exist");

          when(principalRepository.findByLogin(UNKNOWN_LOGIN)).thenReturn(Optional.empty());

          result = service.getPrincipalByLogin(UNKNOWN_LOGIN);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(principalRepository).findByLogin(UNKNOWN_LOGIN);
          verifyNoMoreInteractions(principalRepository);
        }
      }
    }

    @Nested
    class WhenGettingPrincipalByProviderAndExternalId {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting principal by provider and external id");
      }

      @Nested
      class AndThePrincipalExists {
        private Principal principal;
        private Optional<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal exists");

          principal = principal();

          when(principalRepository.findByProviderAndExternalId(PROVIDER, EXTERNAL_ID))
              .thenReturn(Optional.of(principal));

          result = service.getPrincipalByProviderAndExternalId(PROVIDER, EXTERNAL_ID);
        }

        @Test
        void thenItShouldReturnPrincipal() {
          BddLogger.then("it should return principal");

          assertTrue(result.isPresent());
          assertEquals(principal, result.orElseThrow());

          verify(principalRepository).findByProviderAndExternalId(PROVIDER, EXTERNAL_ID);
          verifyNoMoreInteractions(principalRepository);
        }
      }

      @Nested
      class AndThePrincipalDoesNotExist {
        private Optional<Principal> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal does not exist");

          when(principalRepository.findByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID))
              .thenReturn(Optional.empty());

          result = service.getPrincipalByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(principalRepository).findByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID);
          verifyNoMoreInteractions(principalRepository);
        }
      }
    }
  }

  private Principal principal() {
    return new Principal(
        PRINCIPAL_ID,
        LOGIN,
        PROVIDER,
        EXTERNAL_ID,
        USER_ID,
        Set.of(new Structure(STRUCTURE_ID, "RECIA", "Structure description")));
  }
}
