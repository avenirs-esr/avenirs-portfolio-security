package fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.seeder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import fr.avenirsesr.portfolio.common.dependency.domain.port.input.DependencyChecker;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder.RBACAssignmentSeeder;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder.RBACCatalogSeeder;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder.PrincipalSeeder;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class SeederOrchestratorTest {

  @Mock private DependencyChecker dependencyChecker;
  @Mock private JdbcTemplate jdbcTemplate;
  @Mock private PrincipalSeeder principalSeeder;
  @Mock private RBACCatalogSeeder rbacCatalogSeeder;
  @Mock private RBACAssignmentSeeder rbacAssignmentSeeder;
  @Mock private SeedingState seedingState;

  private SeederOrchestrator orchestrator;

  @BeforeEach
  void setup() {
    orchestrator =
        new SeederOrchestrator(
            dependencyChecker,
            jdbcTemplate,
            principalSeeder,
            rbacCatalogSeeder,
            rbacAssignmentSeeder,
            seedingState);
  }

  @Nested
  class GivenExistingDataInDatabase {

    @Test
    void thenSeedMissingShouldSeedWithoutClearingTheDatabase() {
      BddLogger.given("a database that already contains data");

      BddLogger.when("seeding the missing data");
      orchestrator.seedMissing();

      BddLogger.then("it should run every seeder in order and never truncate any table");
      InOrder inOrder = inOrder(rbacCatalogSeeder, principalSeeder, rbacAssignmentSeeder);
      inOrder.verify(rbacCatalogSeeder).seed();
      inOrder.verify(principalSeeder).seed();
      inOrder.verify(rbacAssignmentSeeder).seed();
      verifyNoInteractions(jdbcTemplate);
      verify(seedingState).markCompleted();
    }

    @Test
    void thenResetAndSeedShouldStillClearTheDatabase() {
      BddLogger.given("a database that already contains data");

      BddLogger.when("resetting and seeding");
      orchestrator.resetAndSeed();

      BddLogger.then("it should still truncate the tables before seeding");
      verify(jdbcTemplate)
          .queryForList(any(String.class), org.mockito.ArgumentMatchers.eq(String.class), any());
      verify(rbacCatalogSeeder).seed();
    }
  }

  @Nested
  class GivenASeederThatFails {

    @Test
    void thenSeedMissingShouldMarkTheSeedingAsFailedAndRethrow() {
      BddLogger.given("a principal seeder that fails");
      IllegalStateException failure = new IllegalStateException("boom");
      doThrow(failure).when(principalSeeder).seed();

      BddLogger.when("seeding the missing data");
      assertThatThrownBy(() -> orchestrator.seedMissing()).isSameAs(failure);

      BddLogger.then("it should mark the seeding as failed and skip the assignments");
      verify(seedingState).markFailed(failure);
      verify(seedingState, never()).markCompleted();
      verify(rbacAssignmentSeeder, never()).seed();
      verifyNoInteractions(jdbcTemplate);
    }
  }

  @Nested
  class GivenASeedingAlreadyRunning {

    @Test
    void thenSeedMissingShouldBeRejectedWhenCalledFromAnotherThread() {
      BddLogger.given("a seeding already running and holding the lock");
      AtomicReference<Throwable> concurrentFailure = new AtomicReference<>();
      doAnswer(
              invocation -> {
                Thread other =
                    new Thread(
                        () -> {
                          try {
                            orchestrator.seedMissing();
                          } catch (Throwable t) {
                            concurrentFailure.set(t);
                          }
                        });
                other.start();
                other.join();
                return null;
              })
          .when(rbacCatalogSeeder)
          .seed();

      BddLogger.when("a second seeding is requested concurrently");
      orchestrator.seedMissing();

      BddLogger.then("the concurrent request should be rejected");
      assertThat(concurrentFailure.get())
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("Security seeding already running");
    }
  }
}
