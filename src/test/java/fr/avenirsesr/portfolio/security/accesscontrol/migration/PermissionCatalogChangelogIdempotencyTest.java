package fr.avenirsesr.portfolio.security.accesscontrol.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.avenirsesr.portfolio.security.accesscontrol.PermissionCatalogFixture;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises the real production Liquibase data changelogs (db/changelog/data) directly through the
 * Liquibase Java API against isolated H2 databases, independently of the Spring context.
 *
 * <p>The project has no Testcontainers/real-Postgres test setup. Two pre-existing, unrelated
 * incompatibilities were found while writing these tests, both documented in the task report:
 *
 * <ul>
 *   <li>the real Hibernate-diff generated schema changelogs use a PostgreSQL-only {@code citext}
 *       column type (on {@code action_route.method}), which H2 cannot create at all - so these
 *       tests provision only the "permission" table they actually need, via plain JDBC or a minimal
 *       test-only {@code createTable} changeset, instead of the real generated/ folder;
 *   <li>running Liquibase.update() a second time against a connection that already has a populated
 *       {@code databasechangelog} table fails on H2 in {@code MODE=PostgreSQL} with a spurious
 *       "table already exists" error while creating that same tracking table again - Liquibase's
 *       own re-detection of its tracking table is unreliable on this H2 mode. Because of this,
 *       "replay" scenarios here are validated as a single, first-ever Liquibase run against a
 *       connection that was pre-seeded via plain JDBC (not via a prior Liquibase run), which is
 *       exactly the scenario the idempotence preconditions are meant to protect.
 * </ul>
 */
class PermissionCatalogChangelogIdempotencyTest {

  private static final String CATALOG_ONLY_CHANGELOG =
      "db/changelog/catalog-only-test-changelog.xml";
  private static final int EXPECTED_PERMISSION_COUNT = PermissionCatalogFixture.ALL.size();

  private Connection connection;

  @AfterEach
  void closeConnection() throws Exception {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  void
      givenAnEmptyPermissionTable_whenRunningTheCatalogChangelogsOnce_thenAllExpectedPermissionsAreInserted()
          throws Exception {
    connection = newIsolatedH2Connection("idempotency_full_catalog");
    createPermissionTable(connection);

    newLiquibase(CATALOG_ONLY_CHANGELOG, connection).update("");

    assertThat(countPermissions(connection)).isEqualTo(EXPECTED_PERMISSION_COUNT);
  }

  @Test
  void
      givenAPreExistingPermissionMatchingTheExpectedCatalogEntry_whenRunningTheCatalogChangelogsOnce_thenTheChangeSetIsSkippedWithoutError()
          throws Exception {
    connection = newIsolatedH2Connection("idempotency_exact_match");
    createPermissionTable(connection);
    insertPermission(
        connection,
        "a1000000-0000-0000-0000-000000000001",
        "PERM_PROFILE_READ_OWN",
        "Consulter son propre profil");

    newLiquibase(CATALOG_ONLY_CHANGELOG, connection).update("");

    assertThat(countPermissions(connection)).isEqualTo(EXPECTED_PERMISSION_COUNT);
    assertThat(findPermissionId(connection, "PERM_PROFILE_READ_OWN"))
        .isEqualTo(UUID.fromString("a1000000-0000-0000-0000-000000000001"));
  }

  @Test
  void
      givenAPreExistingPermissionWithTheSameNameButDifferentContent_whenRunningTheCatalogChangelogsOnce_thenItFailsExplicitly()
          throws Exception {
    connection = newIsolatedH2Connection("idempotency_conflict");
    createPermissionTable(connection);
    insertPermission(
        connection,
        "ffffffff-0000-0000-0000-000000000001",
        "PERM_PROFILE_READ_OWN",
        "Description manuelle divergente");

    Liquibase liquibase = newLiquibase(CATALOG_ONLY_CHANGELOG, connection);

    LiquibaseException exception =
        assertThrows(LiquibaseException.class, () -> liquibase.update(""));

    assertThat(rootCauseMessage(exception)).containsIgnoringCase("unique");
  }

  private void createPermissionTable(Connection conn) throws Exception {
    try (Statement statement = conn.createStatement()) {
      statement.execute(
          """
          CREATE TABLE permission (
            id UUID NOT NULL PRIMARY KEY,
            name VARCHAR(80) NOT NULL UNIQUE,
            description VARCHAR(255) NOT NULL
          )
          """);
    }
  }

  private Liquibase newLiquibase(String changeLogFile, Connection conn) throws Exception {
    Database database =
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
    return new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
  }

  private Connection newIsolatedH2Connection(String dbName) throws Exception {
    return DriverManager.getConnection(
        "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "sa",
        "sa");
  }

  private long countPermissions(Connection conn) throws Exception {
    try (Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM permission")) {
      resultSet.next();
      return resultSet.getLong(1);
    }
  }

  private UUID findPermissionId(Connection conn, String name) throws Exception {
    try (Statement statement = conn.createStatement();
        ResultSet resultSet =
            statement.executeQuery("SELECT id FROM permission WHERE name = '" + name + "'")) {
      resultSet.next();
      return UUID.fromString(resultSet.getString(1));
    }
  }

  private void insertPermission(Connection conn, String id, String name, String description)
      throws Exception {
    try (Statement statement = conn.createStatement()) {
      statement.execute(
          "INSERT INTO permission (id, name, description) VALUES ('"
              + id
              + "', '"
              + name
              + "', '"
              + description
              + "')");
    }
  }

  private String rootCauseMessage(Throwable throwable) {
    Throwable current = throwable;
    while (current.getCause() != null) {
      current = current.getCause();
    }
    return current.getMessage();
  }
}
