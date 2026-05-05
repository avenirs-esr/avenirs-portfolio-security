package fr.avenirsesr.portfolio.security.shared.infrastructure.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.security.model.Principal;
import fr.avenirsesr.portfolio.security.seeder.PrincipalSeeder;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederOrchestrator {
  @Value("${seeder.schema:dev}")
  private String schemaName;

  private final ReentrantLock lock = new ReentrantLock();
  private final JdbcTemplate jdbcTemplate;

  private final PrincipalSeeder principalSeeder;
  private final SeedingState seedingState;

  @Transactional
  public void seedAll() {
    try {
      log.info("Security seeding enabled and starting...");

      List<Principal> principalsSaved = principalSeeder.seed();

      log.info("✔ Security seeding successfully finished");
      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Security seeding failed", e);
      throw e;
    }
  }

  @Transactional
  public void clearAll() {
    List<String> tables =
        jdbcTemplate.queryForList(
            """
                SELECT tablename
                FROM pg_tables
                WHERE schemaname = ?
                  AND tablename NOT IN ('databasechangelog', 'databasechangeloglock')
                """,
            String.class,
            schemaName);

    if (tables.isEmpty()) {
      log.warn("No tables found in security schema '{}'", schemaName);
      return;
    }

    String joined =
        tables.stream()
            .map(table -> "\"" + schemaName + "\".\"" + table + "\"")
            .reduce((left, right) -> left + ", " + right)
            .orElseThrow();

    String sql = "TRUNCATE TABLE " + joined + " RESTART IDENTITY CASCADE";

    log.warn("Resetting security DB: {}", sql);
    jdbcTemplate.execute(sql);
  }

  public void resetAndSeed() {
    if (!lock.tryLock()) {
      throw new IllegalStateException("Security seeding already running");
    }

    try {
      clearAll();
      seedAll();
    } finally {
      lock.unlock();
    }
  }
}
