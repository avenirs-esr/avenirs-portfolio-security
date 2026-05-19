package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
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
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GivenARBACContextService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC context service");
    }

    @Nested
    class WhenGettingAllContexts {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all contexts");
      }

      @Nested
      class AndTheRepositoryContainsContexts {
        private RBACContext context;
        private List<RBACContext> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains contexts");

          context = context();

          when(contextRepository.findAll()).thenReturn(List.of(context));

          result = service.getAllContexts();
        }

        @Test
        void thenItShouldReturnRepositoryContexts() {
          BddLogger.then("it should return repository contexts");

          assertThat(result).containsExactly(context);

          verify(contextRepository).findAll();
          verifyNoMoreInteractions(contextRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACContext> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(contextRepository.findAll()).thenReturn(List.of());

          result = service.getAllContexts();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(contextRepository).findAll();
          verifyNoMoreInteractions(contextRepository);
        }
      }
    }

    @Nested
    class WhenGettingContextById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting context by id");
      }

      @Nested
      class AndTheContextExists {
        private RBACContext context;
        private Optional<RBACContext> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the context exists");

          context = context();

          when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.of(context));

          result = service.getContextById(CONTEXT_ID);
        }

        @Test
        void thenItShouldReturnTheContext() {
          BddLogger.then("it should return the context");

          assertTrue(result.isPresent());
          assertEquals(context, result.orElseThrow());

          verify(contextRepository).findById(CONTEXT_ID);
          verifyNoMoreInteractions(contextRepository);
        }
      }

      @Nested
      class AndTheContextDoesNotExist {
        private Optional<RBACContext> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the context does not exist");

          when(contextRepository.findById(UNKNOWN_CONTEXT_ID)).thenReturn(Optional.empty());

          result = service.getContextById(UNKNOWN_CONTEXT_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(contextRepository).findById(UNKNOWN_CONTEXT_ID);
          verifyNoMoreInteractions(contextRepository);
        }
      }
    }

    @Nested
    class WhenCreatingContext {
      private RBACContext contextToCreate;
      private RBACContext savedContext;
      private RBACContext result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating context");

        contextToCreate = contextWithoutId();
        savedContext = context();

        when(contextRepository.save(contextToCreate)).thenReturn(savedContext);

        result = service.createContext(contextToCreate);
      }

      @Test
      void thenItShouldSaveContext() {
        BddLogger.then("it should save context");

        assertEquals(savedContext, result);
        assertEquals(CONTEXT_ID, result.id());

        verify(contextRepository).save(contextToCreate);
        verifyNoMoreInteractions(contextRepository);
      }
    }

    @Nested
    class WhenUpdatingContext {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating context");
      }

      @Nested
      class AndTheContextExists {
        private RBACContext context;
        private RBACContext result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the context exists");

          context = context();

          when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.of(context));
          when(contextRepository.save(context)).thenReturn(context);

          result = service.updateContext(context);
        }

        @Test
        void thenItShouldSaveContext() {
          BddLogger.then("it should save context");

          assertEquals(context, result);

          verify(contextRepository).findById(CONTEXT_ID);
          verify(contextRepository).save(context);
          verifyNoMoreInteractions(contextRepository);
        }
      }

      @Nested
      class AndTheContextDoesNotExist {
        private RBACContext context;
        private AccessControlNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the context does not exist");

          context = context();

          when(contextRepository.findById(CONTEXT_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(
                  AccessControlNotFoundException.class, () -> service.updateContext(context));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertEquals(
              "Context not found, ID: 00000000-0000-0000-0000-000000000001",
              exception.getMessage());

          verify(contextRepository).findById(CONTEXT_ID);
          verifyNoMoreInteractions(contextRepository);
        }
      }
    }

    @Nested
    class WhenDeletingContext {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting context");

        service.deleteContext(CONTEXT_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(contextRepository).deleteById(CONTEXT_ID);
        verifyNoMoreInteractions(contextRepository);
      }
    }
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
