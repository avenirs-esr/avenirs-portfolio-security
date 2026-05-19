package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
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
class RBACAssignmentServiceImplTest {

  private static final UUID ASSIGNMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_ASSIGNMENT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final UUID PRINCIPAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000301");

  @Mock private RBACAssignmentRepository assignmentRepository;

  private RBACAssignmentServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACAssignmentServiceImpl(assignmentRepository);
  }

  @Nested
  class GivenARBACAssignmentService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC assignment service");
    }

    @Nested
    class WhenGettingAllAssignments {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all assignments");
      }

      @Nested
      class AndTheRepositoryContainsAssignments {
        private RBACAssignment assignment;
        private List<RBACAssignment> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains assignments");

          assignment = assignment();

          when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

          result = service.getAllAssignments();
        }

        @Test
        void thenItShouldReturnRepositoryAssignments() {
          BddLogger.then("it should return repository assignments");

          assertThat(result).containsExactly(assignment);

          verify(assignmentRepository).findAll();
          verifyNoMoreInteractions(assignmentRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACAssignment> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(assignmentRepository.findAll()).thenReturn(List.of());

          result = service.getAllAssignments();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(assignmentRepository).findAll();
          verifyNoMoreInteractions(assignmentRepository);
        }
      }
    }

    @Nested
    class WhenGettingAssignmentById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting an assignment by id");
      }

      @Nested
      class AndTheAssignmentExists {
        private RBACAssignment assignment;
        private Optional<RBACAssignment> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the assignment exists");

          assignment = assignment();

          when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.of(assignment));

          result = service.getAssignmentById(ASSIGNMENT_ID);
        }

        @Test
        void thenItShouldReturnTheAssignment() {
          BddLogger.then("it should return the assignment");

          assertTrue(result.isPresent());
          assertEquals(assignment, result.orElseThrow());

          verify(assignmentRepository).findById(ASSIGNMENT_ID);
          verifyNoMoreInteractions(assignmentRepository);
        }
      }

      @Nested
      class AndTheAssignmentDoesNotExist {
        private Optional<RBACAssignment> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the assignment does not exist");

          when(assignmentRepository.findById(UNKNOWN_ASSIGNMENT_ID)).thenReturn(Optional.empty());

          result = service.getAssignmentById(UNKNOWN_ASSIGNMENT_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(assignmentRepository).findById(UNKNOWN_ASSIGNMENT_ID);
          verifyNoMoreInteractions(assignmentRepository);
        }
      }
    }

    @Nested
    class WhenCreatingAssignment {
      private RBACAssignment assignmentToCreate;
      private RBACAssignment savedAssignment;
      private RBACAssignment result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating an assignment");

        assignmentToCreate = assignmentWithoutId();
        savedAssignment = assignment();

        when(assignmentRepository.save(assignmentToCreate)).thenReturn(savedAssignment);

        result = service.createAssignment(assignmentToCreate);
      }

      @Test
      void thenItShouldSaveTheAssignment() {
        BddLogger.then("it should save the assignment");

        assertEquals(savedAssignment, result);
        assertEquals(ASSIGNMENT_ID, result.id());

        verify(assignmentRepository).save(assignmentToCreate);
        verifyNoMoreInteractions(assignmentRepository);
      }
    }

    @Nested
    class WhenUpdatingAssignment {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating an assignment");
      }

      @Nested
      class AndTheAssignmentExists {
        private RBACAssignment assignment;
        private RBACAssignment result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the assignment exists");

          assignment = assignment();

          when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.of(assignment));
          when(assignmentRepository.save(assignment)).thenReturn(assignment);

          result = service.updateAssignment(assignment);
        }

        @Test
        void thenItShouldSaveTheAssignment() {
          BddLogger.then("it should save the assignment");

          assertEquals(assignment, result);

          verify(assignmentRepository).findById(ASSIGNMENT_ID);
          verify(assignmentRepository).save(assignment);
          verifyNoMoreInteractions(assignmentRepository);
        }
      }

      @Nested
      class AndTheAssignmentDoesNotExist {
        private RBACAssignment assignment;
        private AccessControlNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the assignment does not exist");

          assignment = assignment();

          when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(
                  AccessControlNotFoundException.class, () -> service.updateAssignment(assignment));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertEquals(
              "Assignment not found, ID: 00000000-0000-0000-0000-000000000001",
              exception.getMessage());

          verify(assignmentRepository).findById(ASSIGNMENT_ID);
          verifyNoMoreInteractions(assignmentRepository);
        }
      }
    }

    @Nested
    class WhenDeletingAssignment {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting an assignment");

        service.deleteAssignment(ASSIGNMENT_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(assignmentRepository).deleteById(ASSIGNMENT_ID);
        verifyNoMoreInteractions(assignmentRepository);
      }
    }
  }

  private RBACAssignment assignment() {
    return new RBACAssignment(
        ASSIGNMENT_ID, principal(), role(), scope(), new RBACContext(null, null, null, Set.of()));
  }

  private RBACAssignment assignmentWithoutId() {
    return new RBACAssignment(
        null, principal(), role(), scope(), new RBACContext(null, null, null, Set.of()));
  }

  private Principal principal() {
    return new Principal(PRINCIPAL_ID, "deman", "OIDC", "deman", UUID.randomUUID(), Set.of());
  }

  private RBACRole role() {
    return new RBACRole(
        ROLE_ID,
        "ROLE_OWNER",
        "Owner of the resource",
        Set.of(new RBACPermission(UUID.randomUUID(), "PERM_READ", "Read permission")));
  }

  private RBACScope scope() {
    return new RBACScope(null, List.of(new RBACResource(RESOURCE_ID, "ptf_0000", resourceType())));
  }

  private RBACResourceType resourceType() {
    return new RBACResourceType(
        UUID.fromString("00000000-0000-0000-0000-000000000401"),
        "PORTFOLIO",
        "Resource of type portfolio");
  }
}
