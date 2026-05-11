package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
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

  @Test
  void getAllAssignmentsReturnsRepositoryAssignments() {
    RBACAssignment assignment = assignment();

    when(assignmentRepository.findAll()).thenReturn(List.of(assignment));

    List<RBACAssignment> result = service.getAllAssignments();

    assertThat(result).containsExactly(assignment);

    verify(assignmentRepository).findAll();
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void getAllAssignmentsReturnsEmptyListWhenRepositoryIsEmpty() {
    when(assignmentRepository.findAll()).thenReturn(List.of());

    List<RBACAssignment> result = service.getAllAssignments();

    assertThat(result).isEmpty();

    verify(assignmentRepository).findAll();
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void getAssignmentByIdReturnsAssignmentWhenFound() {
    RBACAssignment assignment = assignment();

    when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.of(assignment));

    Optional<RBACAssignment> result = service.getAssignmentById(ASSIGNMENT_ID);

    assertTrue(result.isPresent());
    assertEquals(assignment, result.orElseThrow());

    verify(assignmentRepository).findById(ASSIGNMENT_ID);
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void getAssignmentByIdReturnsEmptyWhenNotFound() {
    when(assignmentRepository.findById(UNKNOWN_ASSIGNMENT_ID)).thenReturn(Optional.empty());

    Optional<RBACAssignment> result = service.getAssignmentById(UNKNOWN_ASSIGNMENT_ID);

    assertTrue(result.isEmpty());

    verify(assignmentRepository).findById(UNKNOWN_ASSIGNMENT_ID);
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void createAssignmentSavesAssignment() {
    RBACAssignment assignmentToCreate = assignmentWithoutId();
    RBACAssignment savedAssignment = assignment();

    when(assignmentRepository.save(assignmentToCreate)).thenReturn(savedAssignment);

    RBACAssignment result = service.createAssignment(assignmentToCreate);

    assertEquals(savedAssignment, result);
    assertEquals(ASSIGNMENT_ID, result.id());

    verify(assignmentRepository).save(assignmentToCreate);
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void updateAssignmentSavesAssignmentWhenExistingAssignmentIsFound() {
    RBACAssignment assignment = assignment();

    when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.of(assignment));
    when(assignmentRepository.save(assignment)).thenReturn(assignment);

    RBACAssignment result = service.updateAssignment(assignment);

    assertEquals(assignment, result);

    verify(assignmentRepository).findById(ASSIGNMENT_ID);
    verify(assignmentRepository).save(assignment);
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void updateAssignmentThrowsWhenAssignmentDoesNotExist() {
    RBACAssignment assignment = assignment();

    when(assignmentRepository.findById(ASSIGNMENT_ID)).thenReturn(Optional.empty());

    AccessControlNotFoundException exception =
        assertThrows(
            AccessControlNotFoundException.class, () -> service.updateAssignment(assignment));

    assertEquals(
        "Assignment not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

    verify(assignmentRepository).findById(ASSIGNMENT_ID);
    verifyNoMoreInteractions(assignmentRepository);
  }

  @Test
  void deleteAssignmentDeletesById() {
    service.deleteAssignment(ASSIGNMENT_ID);

    verify(assignmentRepository).deleteById(ASSIGNMENT_ID);
    verifyNoMoreInteractions(assignmentRepository);
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
