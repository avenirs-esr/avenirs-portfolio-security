package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACScopeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RBACScopeServiceImplTest {

  private static final UUID SCOPE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_SCOPE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
  private static final UUID RESOURCE_TYPE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000020");

  private static final String SCOPE_NAME = "scope_0001";
  private static final String UNKNOWN_SCOPE_NAME = "scope_unknown";

  @Mock private RBACScopeRepository scopeRepository;

  private RBACScopeServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACScopeServiceImpl(scopeRepository);
  }

  @Test
  void getScopeByIdReturnsScopeWhenFound() {
    RBACScope scope = scope();

    when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.of(scope));

    Optional<RBACScope> result = service.getScopeById(SCOPE_ID);

    assertTrue(result.isPresent());
    assertEquals(scope, result.orElseThrow());

    verify(scopeRepository).findById(SCOPE_ID);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void getScopeByIdReturnsEmptyWhenNotFound() {
    when(scopeRepository.findById(UNKNOWN_SCOPE_ID)).thenReturn(Optional.empty());

    Optional<RBACScope> result = service.getScopeById(UNKNOWN_SCOPE_ID);

    assertTrue(result.isEmpty());

    verify(scopeRepository).findById(UNKNOWN_SCOPE_ID);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void getScopeByNameReturnsScopeWhenFound() {
    RBACScope scope = scope();

    when(scopeRepository.findByName(SCOPE_NAME)).thenReturn(Optional.of(scope));

    Optional<RBACScope> result = service.getScopeByName(SCOPE_NAME);

    assertTrue(result.isPresent());
    assertEquals(scope, result.orElseThrow());

    verify(scopeRepository).findByName(SCOPE_NAME);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void getScopeByNameReturnsEmptyWhenNotFound() {
    when(scopeRepository.findByName(UNKNOWN_SCOPE_NAME)).thenReturn(Optional.empty());

    Optional<RBACScope> result = service.getScopeByName(UNKNOWN_SCOPE_NAME);

    assertTrue(result.isEmpty());

    verify(scopeRepository).findByName(UNKNOWN_SCOPE_NAME);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void getAllScopesReturnsRepositoryScopes() {
    RBACScope scope = scope();

    when(scopeRepository.findAll()).thenReturn(List.of(scope));

    List<RBACScope> result = service.getAllScopes();

    assertThat(result).containsExactly(scope);

    verify(scopeRepository).findAll();
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void getAllScopesReturnsEmptyListWhenRepositoryIsEmpty() {
    when(scopeRepository.findAll()).thenReturn(List.of());

    List<RBACScope> result = service.getAllScopes();

    assertThat(result).isEmpty();

    verify(scopeRepository).findAll();
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void createScopeSavesScope() {
    RBACScope scopeToCreate = scopeWithoutId();
    RBACScope savedScope = scope();

    when(scopeRepository.save(scopeToCreate)).thenReturn(savedScope);

    RBACScope result = service.createScope(scopeToCreate);

    assertEquals(savedScope, result);
    assertEquals(SCOPE_ID, result.id());

    verify(scopeRepository).save(scopeToCreate);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void updateScopeSavesScopeWhenExistingScopeIsFound() {
    RBACScope scope = scope();

    when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.of(scope));
    when(scopeRepository.save(scope)).thenReturn(scope);

    RBACScope result = service.updateScope(scope);

    assertEquals(scope, result);

    verify(scopeRepository).findById(SCOPE_ID);
    verify(scopeRepository).save(scope);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void updateScopeThrowsWhenScopeDoesNotExist() {
    RBACScope scope = scope();

    when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.empty());

    AccessControlNotFoundException exception =
        assertThrows(AccessControlNotFoundException.class, () -> service.updateScope(scope));

    assertEquals(
        "Scope not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

    verify(scopeRepository).findById(SCOPE_ID);
    verifyNoMoreInteractions(scopeRepository);
  }

  @Test
  void deleteScopeDeletesById() {
    service.deleteScope(SCOPE_ID);

    verify(scopeRepository).deleteById(SCOPE_ID);
    verifyNoMoreInteractions(scopeRepository);
  }

  private RBACScope scope() {
    return new RBACScope(SCOPE_ID, List.of(resource()));
  }

  private RBACScope scopeWithoutId() {
    return new RBACScope(null, List.of(resource()));
  }

  private RBACResource resource() {
    return new RBACResource(
        RESOURCE_ID,
        "test_scope_resource",
        new RBACResourceType(RESOURCE_TYPE_ID, "PORTFOLIO", "Resource of type portfolio"));
  }
}
