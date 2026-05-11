package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RBACActionServiceImplTest {

  private static final UUID ACTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_ACTION_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final String ACTION_NAME = "ACT_SHARE_READ_RESOURCE";
  private static final String UNKNOWN_ACTION_NAME = "ACT_UNKNOWN";

  @Mock private RBACActionRepository actionRepository;

  private RBACActionServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACActionServiceImpl(actionRepository);
  }

  @Test
  void getActionByIdReturnsActionWhenFound() {
    RBACAction expectedAction =
        new RBACAction(
            ACTION_ID,
            ACTION_NAME,
            "Share a resource readonly",
            List.of(new RBACPermission(UUID.randomUUID(), "PERM_SHARE", "Share permission")));

    when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(expectedAction));

    Optional<RBACAction> result = service.getActionById(ACTION_ID);

    assertTrue(result.isPresent());

    RBACAction actual = result.orElseThrow();
    assertEquals(ACTION_ID, actual.id());
    assertEquals(ACTION_NAME, actual.name());
    assertThat(actual.permissions()).extracting(RBACPermission::name).containsExactly("PERM_SHARE");

    verify(actionRepository).findById(ACTION_ID);
    verifyNoMoreInteractions(actionRepository);
  }

  @Test
  void getActionByIdReturnsEmptyWhenNotFound() {
    when(actionRepository.findById(UNKNOWN_ACTION_ID)).thenReturn(Optional.empty());

    Optional<RBACAction> result = service.getActionById(UNKNOWN_ACTION_ID);

    assertTrue(result.isEmpty());

    verify(actionRepository).findById(UNKNOWN_ACTION_ID);
    verifyNoMoreInteractions(actionRepository);
  }

  @Test
  void getActionByNameReturnsActionWhenFound() {
    RBACAction expectedAction =
        new RBACAction(
            ACTION_ID,
            ACTION_NAME,
            "Share a resource readonly",
            List.of(new RBACPermission(UUID.randomUUID(), "PERM_SHARE", "Share permission")));

    when(actionRepository.findByName(ACTION_NAME)).thenReturn(Optional.of(expectedAction));

    Optional<RBACAction> result = service.getActionByName(ACTION_NAME);

    assertTrue(result.isPresent());

    RBACAction actual = result.orElseThrow();
    assertEquals(ACTION_ID, actual.id());
    assertEquals(ACTION_NAME, actual.name());
    assertThat(actual.permissions()).extracting(RBACPermission::name).containsExactly("PERM_SHARE");

    verify(actionRepository).findByName(ACTION_NAME);
    verifyNoMoreInteractions(actionRepository);
  }

  @Test
  void getActionByNameReturnsEmptyWhenNotFound() {
    when(actionRepository.findByName(UNKNOWN_ACTION_NAME)).thenReturn(Optional.empty());

    Optional<RBACAction> result = service.getActionByName(UNKNOWN_ACTION_NAME);

    assertTrue(result.isEmpty());

    verify(actionRepository).findByName(UNKNOWN_ACTION_NAME);
    verifyNoMoreInteractions(actionRepository);
  }

  @Test
  void getAllActionsReturnsRepositoryActions() {
    RBACAction shareRead =
        new RBACAction(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "ACT_SHARE_READ_RESOURCE",
            "Share a resource readonly",
            List.of(new RBACPermission(UUID.randomUUID(), "PERM_SHARE", "Share permission")));

    RBACAction feedback =
        new RBACAction(
            UUID.fromString("00000000-0000-0000-0000-000000000005"),
            "ACT_DO_FEEDBACK",
            "Do a feedback",
            List.of(
                new RBACPermission(UUID.randomUUID(), "PERM_READ", "Read permission"),
                new RBACPermission(UUID.randomUUID(), "PERM_COMMENT", "Comments and feedbacks")));

    RBACAction delete =
        new RBACAction(
            UUID.fromString("00000000-0000-0000-0000-000000000006"),
            "ACT_DELETE",
            "Delete a resource",
            List.of(
                new RBACPermission(UUID.randomUUID(), "PERM_READ", "Read permission"),
                new RBACPermission(UUID.randomUUID(), "PERM_DELETE", "Delete permission")));

    when(actionRepository.findAll()).thenReturn(List.of(shareRead, feedback, delete));

    List<RBACAction> result = service.getAllActions();

    assertThat(result).hasSize(3);
    assertThat(result)
        .extracting(RBACAction::name)
        .containsExactly("ACT_SHARE_READ_RESOURCE", "ACT_DO_FEEDBACK", "ACT_DELETE");

    verify(actionRepository).findAll();
    verifyNoMoreInteractions(actionRepository);
  }

  @Test
  void getAllActionsReturnsEmptyListWhenRepositoryIsEmpty() {
    when(actionRepository.findAll()).thenReturn(List.of());

    List<RBACAction> result = service.getAllActions();

    assertThat(result).isEmpty();

    verify(actionRepository).findAll();
    verifyNoMoreInteractions(actionRepository);
  }
}
