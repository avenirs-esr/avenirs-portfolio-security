package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACScopeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GivenARBACScopeService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC scope service");
    }

    @Nested
    class WhenGettingScopeById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting scope by id");
      }

      @Nested
      class AndTheScopeExists {
        private RBACScope scope;
        private Optional<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope exists");

          scope = scope();

          when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.of(scope));

          result = service.getScopeById(SCOPE_ID);
        }

        @Test
        void thenItShouldReturnScope() {
          BddLogger.then("it should return scope");

          assertTrue(result.isPresent());
          assertEquals(scope, result.orElseThrow());

          verify(scopeRepository).findById(SCOPE_ID);
          verifyNoMoreInteractions(scopeRepository);
        }
      }

      @Nested
      class AndTheScopeDoesNotExist {
        private Optional<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope does not exist");

          when(scopeRepository.findById(UNKNOWN_SCOPE_ID)).thenReturn(Optional.empty());

          result = service.getScopeById(UNKNOWN_SCOPE_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(scopeRepository).findById(UNKNOWN_SCOPE_ID);
          verifyNoMoreInteractions(scopeRepository);
        }
      }
    }

    @Nested
    class WhenGettingScopeByName {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting scope by name");
      }

      @Nested
      class AndTheScopeExists {
        private RBACScope scope;
        private Optional<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope exists");

          scope = scope();

          when(scopeRepository.findByName(SCOPE_NAME)).thenReturn(Optional.of(scope));

          result = service.getScopeByName(SCOPE_NAME);
        }

        @Test
        void thenItShouldReturnScope() {
          BddLogger.then("it should return scope");

          assertTrue(result.isPresent());
          assertEquals(scope, result.orElseThrow());

          verify(scopeRepository).findByName(SCOPE_NAME);
          verifyNoMoreInteractions(scopeRepository);
        }
      }

      @Nested
      class AndTheScopeDoesNotExist {
        private Optional<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope does not exist");

          when(scopeRepository.findByName(UNKNOWN_SCOPE_NAME)).thenReturn(Optional.empty());

          result = service.getScopeByName(UNKNOWN_SCOPE_NAME);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(scopeRepository).findByName(UNKNOWN_SCOPE_NAME);
          verifyNoMoreInteractions(scopeRepository);
        }
      }
    }

    @Nested
    class WhenGettingAllScopes {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all scopes");
      }

      @Nested
      class AndTheRepositoryContainsScopes {
        private RBACScope scope;
        private List<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains scopes");

          scope = scope();

          when(scopeRepository.findAll()).thenReturn(List.of(scope));

          result = service.getAllScopes();
        }

        @Test
        void thenItShouldReturnRepositoryScopes() {
          BddLogger.then("it should return repository scopes");

          assertThat(result).containsExactly(scope);

          verify(scopeRepository).findAll();
          verifyNoMoreInteractions(scopeRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACScope> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(scopeRepository.findAll()).thenReturn(List.of());

          result = service.getAllScopes();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(scopeRepository).findAll();
          verifyNoMoreInteractions(scopeRepository);
        }
      }
    }

    @Nested
    class WhenCreatingScope {
      private RBACScope scopeToCreate;
      private RBACScope savedScope;
      private RBACScope result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating scope");

        scopeToCreate = scopeWithoutId();
        savedScope = scope();

        when(scopeRepository.save(scopeToCreate)).thenReturn(savedScope);

        result = service.createScope(scopeToCreate);
      }

      @Test
      void thenItShouldSaveScope() {
        BddLogger.then("it should save scope");

        assertEquals(savedScope, result);
        assertEquals(SCOPE_ID, result.id());

        verify(scopeRepository).save(scopeToCreate);
        verifyNoMoreInteractions(scopeRepository);
      }
    }

    @Nested
    class WhenUpdatingScope {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating scope");
      }

      @Nested
      class AndTheScopeExists {
        private RBACScope scope;
        private RBACScope result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope exists");

          scope = scope();

          when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.of(scope));
          when(scopeRepository.save(scope)).thenReturn(scope);

          result = service.updateScope(scope);
        }

        @Test
        void thenItShouldSaveScope() {
          BddLogger.then("it should save scope");

          assertEquals(scope, result);

          verify(scopeRepository).findById(SCOPE_ID);
          verify(scopeRepository).save(scope);
          verifyNoMoreInteractions(scopeRepository);
        }
      }

      @Nested
      class AndTheScopeDoesNotExist {
        private RBACScope scope;
        private AccessControlNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the scope does not exist");

          scope = scope();

          when(scopeRepository.findById(SCOPE_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(AccessControlNotFoundException.class, () -> service.updateScope(scope));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertEquals(
              "Scope not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

          verify(scopeRepository).findById(SCOPE_ID);
          verifyNoMoreInteractions(scopeRepository);
        }
      }
    }

    @Nested
    class WhenDeletingScope {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting scope");

        service.deleteScope(SCOPE_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(scopeRepository).deleteById(SCOPE_ID);
        verifyNoMoreInteractions(scopeRepository);
      }
    }
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
