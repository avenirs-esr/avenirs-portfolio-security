package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
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

  @Nested
  class GivenARBACActionService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC action service");
    }

    @Nested
    class WhenGettingActionById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting an action by id");
      }

      @Nested
      class AndTheActionExists {
        private Optional<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action exists");

          RBACAction expectedAction = action();

          when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(expectedAction));

          result = service.getActionById(ACTION_ID);
        }

        @Test
        void thenItShouldReturnTheAction() {
          BddLogger.then("it should return the action");

          assertTrue(result.isPresent());

          RBACAction actual = result.orElseThrow();
          assertEquals(ACTION_ID, actual.id());
          assertEquals(ACTION_NAME, actual.name());
          assertThat(actual.permissions())
              .extracting(RBACPermission::name)
              .containsExactly("PERM_SHARE");

          verify(actionRepository).findById(ACTION_ID);
          verifyNoMoreInteractions(actionRepository);
        }
      }

      @Nested
      class AndTheActionDoesNotExist {
        private Optional<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action does not exist");

          when(actionRepository.findById(UNKNOWN_ACTION_ID)).thenReturn(Optional.empty());

          result = service.getActionById(UNKNOWN_ACTION_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(actionRepository).findById(UNKNOWN_ACTION_ID);
          verifyNoMoreInteractions(actionRepository);
        }
      }
    }

    @Nested
    class WhenGettingActionByName {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting an action by name");
      }

      @Nested
      class AndTheActionExists {
        private Optional<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action exists");

          RBACAction expectedAction = action();

          when(actionRepository.findByName(ACTION_NAME)).thenReturn(Optional.of(expectedAction));

          result = service.getActionByName(ACTION_NAME);
        }

        @Test
        void thenItShouldReturnTheAction() {
          BddLogger.then("it should return the action");

          assertTrue(result.isPresent());

          RBACAction actual = result.orElseThrow();
          assertEquals(ACTION_ID, actual.id());
          assertEquals(ACTION_NAME, actual.name());
          assertThat(actual.permissions())
              .extracting(RBACPermission::name)
              .containsExactly("PERM_SHARE");

          verify(actionRepository).findByName(ACTION_NAME);
          verifyNoMoreInteractions(actionRepository);
        }
      }

      @Nested
      class AndTheActionDoesNotExist {
        private Optional<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action does not exist");

          when(actionRepository.findByName(UNKNOWN_ACTION_NAME)).thenReturn(Optional.empty());

          result = service.getActionByName(UNKNOWN_ACTION_NAME);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(actionRepository).findByName(UNKNOWN_ACTION_NAME);
          verifyNoMoreInteractions(actionRepository);
        }
      }
    }

    @Nested
    class WhenGettingAllActions {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all actions");
      }

      @Nested
      class AndTheRepositoryContainsActions {
        private List<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains actions");

          RBACAction shareRead =
              new RBACAction(
                  UUID.fromString("00000000-0000-0000-0000-000000000001"),
                  "ACT_SHARE_READ_RESOURCE",
                  "Share a resource readonly",
                  List.of(permission("PERM_SHARE", "Share permission")));

          RBACAction feedback =
              new RBACAction(
                  UUID.fromString("00000000-0000-0000-0000-000000000005"),
                  "ACT_DO_FEEDBACK",
                  "Do a feedback",
                  List.of(
                      permission("PERM_READ", "Read permission"),
                      permission("PERM_COMMENT", "Comments and feedbacks")));

          RBACAction delete =
              new RBACAction(
                  UUID.fromString("00000000-0000-0000-0000-000000000006"),
                  "ACT_DELETE",
                  "Delete a resource",
                  List.of(
                      permission("PERM_READ", "Read permission"),
                      permission("PERM_DELETE", "Delete permission")));

          when(actionRepository.findAll()).thenReturn(List.of(shareRead, feedback, delete));

          result = service.getAllActions();
        }

        @Test
        void thenItShouldReturnRepositoryActions() {
          BddLogger.then("it should return repository actions");

          assertThat(result).hasSize(3);
          assertThat(result)
              .extracting(RBACAction::name)
              .containsExactly("ACT_SHARE_READ_RESOURCE", "ACT_DO_FEEDBACK", "ACT_DELETE");

          verify(actionRepository).findAll();
          verifyNoMoreInteractions(actionRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACAction> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(actionRepository.findAll()).thenReturn(List.of());

          result = service.getAllActions();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(actionRepository).findAll();
          verifyNoMoreInteractions(actionRepository);
        }
      }
    }
  }

  private RBACAction action() {
    return new RBACAction(
        RBACActionServiceImplTest.ACTION_ID,
        RBACActionServiceImplTest.ACTION_NAME,
        "Share a resource readonly",
        List.of(permission("PERM_SHARE", "Share permission")));
  }

  private RBACPermission permission(String name, String description) {
    return new RBACPermission(UUID.randomUUID(), name, description);
  }
}
