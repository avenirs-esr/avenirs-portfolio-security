package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Principal in the RBAC system. */
@Entity
@Table(
    name = "principal",
    indexes = {
      @Index(name = "principal_eppn_idx", columnList = "eppn"),
      @Index(name = "principal_login_idx", columnList = "login"),
      @Index(name = "principal_external_id_idx", columnList = "external_id"),
      @Index(name = "principal_provider_external_id_idx", columnList = "provider, external_id"),
      @Index(name = "principal_status_idx", columnList = "status")
    },
    uniqueConstraints = {
      @UniqueConstraint(name = "principal_eppn_uk", columnNames = "eppn"),
      @UniqueConstraint(
          name = "principal_provider_external_id_uk",
          columnNames = {"provider", "external_id"})
    })
@NoArgsConstructor
@Getter
@Setter
public class PrincipalEntity extends AvenirsBaseEntity {

  /** Globally unique stable identifier used between microservices. */
  @Column(length = 255, nullable = false, unique = true)
  private String eppn;

  /** Login of the principal. Usually equals eppn or email. */
  @Column(length = 255, nullable = false)
  private String login;

  @Column(name = "password_hash", length = 255)
  private String passwordHash;

  /** Authentication provider, for example CAS, OIDC, PEGASE, APOGEE, LOCAL. */
  @Column(length = 100, nullable = false)
  private String provider;

  /** Stable external identifier from the source/provider. */
  @Column(name = "external_id", length = 255, nullable = false)
  private String externalId;

  /** User category from a security point of view. */
  @Column(length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private EUserCategory category;

  /** Security status of the principal. */
  @Column(length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private EUserStatus status;

  /** Structures associated to the principal. */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "principal_structure",
      joinColumns = @JoinColumn(name = "id_principal", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_structure", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "principal_structure_pk",
              columnNames = {"id_principal", "id_structure"}))
  private Set<StructureEntity> structureEntities = new HashSet<>();

  private PrincipalEntity(
      UUID id,
      String eppn,
      String login,
      String passwordHash,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Set<StructureEntity> structureEntities,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.eppn = eppn;
    this.login = login;
    this.provider = provider;
    this.externalId = externalId;
    this.category = category;
    this.status = status;
    this.structureEntities = structureEntities != null ? structureEntities : new HashSet<>();
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static PrincipalEntity of(
      UUID id,
      String eppn,
      String login,
      String passwordHash,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Set<StructureEntity> structureEntities,
      Instant createdAt,
      Instant updatedAt) {
    return new PrincipalEntity(
        id,
        eppn,
        login,
        passwordHash,
        provider,
        externalId,
        category,
        status,
        structureEntities,
        createdAt,
        updatedAt);
  }

  public static PrincipalEntity of(
      UUID id,
      String eppn,
      String login,
      String passwordHash,
      String provider,
      String externalId,
      EUserCategory category,
      EUserStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new PrincipalEntity(
        id,
        eppn,
        login,
        passwordHash,
        provider,
        externalId,
        category,
        status,
        new HashSet<>(),
        createdAt,
        updatedAt);
  }
}
