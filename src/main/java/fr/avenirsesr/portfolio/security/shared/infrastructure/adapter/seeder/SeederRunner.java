package fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository.PrincipalJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederRunner implements CommandLineRunner {

  private final PrincipalJpaRepository principalJpaRepository;
  private final SeederOrchestrator seederOrchestrator;
  private final SeedingState seedingState;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      log.info("Security seeder disabled: skipped");
      seedingState.markCompleted();
      return;
    }

    seederOrchestrator.resetAndSeed();
  }
}
