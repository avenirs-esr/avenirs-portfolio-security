package fr.avenirsesr.portfolio.security.principal.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Principal extends AvenirsBaseModel {

  private final String eppn;
  private final String login;
  private final String provider;
  private final String externalId;
  private final EUserCategory category;
  private final EUserStatus status;
  private final Set<Structure> structures;

  private Principal(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String eppn,
      String login,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Set<Structure> structures) {
    super(id, createdAt, updatedAt);
    this.eppn = eppn;
    this.login = login;
    this.provider = provider;
    this.externalId = externalId;
    this.category = category;
    this.status = status;
    this.structures = structures == null ? Set.of() : Set.copyOf(structures);
  }

  public static Principal create(
      String eppn,
      String login,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Set<Structure> structures) {
    Instant now = Instant.now();

    return new Principal(
        UUID.randomUUID(),
        now,
        now,
        eppn,
        login,
        provider,
        externalId,
        category,
        status,
        structures);
  }

  public static Principal toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String eppn,
      String login,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Set<Structure> structures) {
    return new Principal(
        id, createdAt, updatedAt, eppn, login, provider, externalId, category, status, structures);
  }

  public boolean isActive() {
    return EUserStatus.ACTIVE.equals(status);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Principal principal = (Principal) o;
    return Objects.equals(eppn, principal.eppn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eppn);
  }

  @Override
  public String toString() {
    return "Principal[eppn=" + eppn + ", provider=" + provider + ", externalId=" + externalId + ']';
  }
}
