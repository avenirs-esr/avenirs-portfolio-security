package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACContextRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import java.time.LocalDateTime;
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
class RBACContextServiceImplTest {

  private static final UUID CONTEXT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_CONTEXT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  @Mock private RBACContextRepository contextRepository;

  private RBACContextServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACContextServiceImpl(contextRepository);
  }

  @Test
  void getAllContextsReturnsRepositoryContexts() {
    RBACContext context = context();

    when(contextRepository.findAll()).thenReturn(List.of(context));

    List<RBACContext> result = service.getAllContexts();

    assertThat(result).containsExactly(context);

    verify(contextRepository).findAll();
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void getAllContextsReturnsEmptyListWhenRepositoryIsEmpty() {
    when(contextRepository.findAll()).thenReturn(List.of());

    List<RBACContext> result = service.getAllContexts();

    assertThat(result).isEmpty();

    verify(contextRepository).findAll();
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void getContextByIdReturnsContextWhenFound() {
    RBACContext context = context();

    when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.of(context));

    Optional<RBACContext> result = service.getContextById(CONTEXT_ID);

    assertTrue(result.isPresent());
    assertEquals(context, result.orElseThrow());

    verify(contextRepository).findById(CONTEXT_ID);
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void getContextByIdReturnsEmptyWhenNotFound() {
    when(contextRepository.findById(UNKNOWN_CONTEXT_ID)).thenReturn(Optional.empty());

    Optional<RBACContext> result = service.getContextById(UNKNOWN_CONTEXT_ID);

    assertTrue(result.isEmpty());

    verify(contextRepository).findById(UNKNOWN_CONTEXT_ID);
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void createContextSavesContext() {
    RBACContext contextToCreate = contextWithoutId();
    RBACContext savedContext = context();

    when(contextRepository.save(contextToCreate)).thenReturn(savedContext);

    RBACContext result = service.createContext(contextToCreate);

    assertEquals(savedContext, result);
    assertEquals(CONTEXT_ID, result.id());

    verify(contextRepository).save(contextToCreate);
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void updateContextSavesContextWhenExistingContextIsFound() {
    RBACContext context = context();

    when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.of(context));
    when(contextRepository.save(context)).thenReturn(context);

    RBACContext result = service.updateContext(context);

    assertEquals(context, result);

    verify(contextRepository).findById(CONTEXT_ID);
    verify(contextRepository).save(context);
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void updateContextThrowsWhenContextDoesNotExist() {
    RBACContext context = context();

    when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.empty());

    AccessControlNotFoundException exception =
        assertThrows(AccessControlNotFoundException.class, () -> service.updateContext(context));

    assertEquals(
        "Context not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

    verify(contextRepository).findById(CONTEXT_ID);
    verifyNoMoreInteractions(contextRepository);
  }

  @Test
  void deleteContextDeletesById() {
    service.deleteContext(CONTEXT_ID);

    verify(contextRepository).deleteById(CONTEXT_ID);
    verifyNoMoreInteractions(contextRepository);
  }

  private RBACContext context() {
    return new RBACContext(
        CONTEXT_ID,
        LocalDateTime.of(2025, 4, 1, 0, 0),
        LocalDateTime.of(2025, 4, 15, 0, 0),
        Set.of(structure()));
  }

  private RBACContext contextWithoutId() {
    return new RBACContext(
        null,
        LocalDateTime.of(2025, 4, 1, 0, 0),
        LocalDateTime.of(2025, 4, 15, 0, 0),
        Set.of(structure()));
  }

  private Structure structure() {
    return new Structure(
        UUID.fromString("00000000-0000-0000-0000-000000000101"),
        "RECIA",
        "Groupement d'Intérêt Public Région Centre Interactive");
  }
}
