package fr.avenirsesr.portfolio.security.principal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  @Test
  void getAllPrincipalsReturnsRepositoryPrincipals() {
    Principal principal = principal();

    when(principalRepository.findAll()).thenReturn(List.of(principal));

    List<Principal> result = service.getAllPrincipals();

    assertThat(result).containsExactly(principal);

    verify(principalRepository).findAll();
    verifyNoMoreInteractions(principalRepository);
  }

  @Test
  void getAllPrincipalsReturnsEmptyListWhenRepositoryIsEmpty() {
    when(principalRepository.findAll()).thenReturn(List.of());

    List<Principal> result = service.getAllPrincipals();

    assertThat(result).isEmpty();

    verify(principalRepository).findAll();
    verifyNoMoreInteractions(principalRepository);
  }

  @Test
  void getPrincipalByLoginReturnsPrincipalWhenFound() {
    Principal principal = principal();

    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));

    Optional<Principal> result = service.getPrincipalByLogin(LOGIN);

    assertTrue(result.isPresent());
    assertEquals(principal, result.orElseThrow());

    verify(principalRepository).findByLogin(LOGIN);
    verifyNoMoreInteractions(principalRepository);
  }

  @Test
  void getPrincipalByLoginReturnsEmptyWhenNotFound() {
    when(principalRepository.findByLogin(UNKNOWN_LOGIN)).thenReturn(Optional.empty());

    Optional<Principal> result = service.getPrincipalByLogin(UNKNOWN_LOGIN);

    assertTrue(result.isEmpty());

    verify(principalRepository).findByLogin(UNKNOWN_LOGIN);
    verifyNoMoreInteractions(principalRepository);
  }

  @Test
  void getPrincipalByProviderAndExternalIdReturnsPrincipalWhenFound() {
    Principal principal = principal();

    when(principalRepository.findByProviderAndExternalId(PROVIDER, EXTERNAL_ID))
        .thenReturn(Optional.of(principal));

    Optional<Principal> result = service.getPrincipalByProviderAndExternalId(PROVIDER, EXTERNAL_ID);

    assertTrue(result.isPresent());
    assertEquals(principal, result.orElseThrow());

    verify(principalRepository).findByProviderAndExternalId(PROVIDER, EXTERNAL_ID);
    verifyNoMoreInteractions(principalRepository);
  }

  @Test
  void getPrincipalByProviderAndExternalIdReturnsEmptyWhenNotFound() {
    when(principalRepository.findByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID))
        .thenReturn(Optional.empty());

    Optional<Principal> result =
        service.getPrincipalByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID);

    assertTrue(result.isEmpty());

    verify(principalRepository).findByProviderAndExternalId(PROVIDER, UNKNOWN_EXTERNAL_ID);
    verifyNoMoreInteractions(principalRepository);
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
