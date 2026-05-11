package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
      @Index(name = "principal_login_idx", columnList = "login"),
      @Index(name = "principal_external_id_idx", columnList = "external_id"),
      @Index(name = "principal_user_id_idx", columnList = "user_id")
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "principal_provider_external_id_uk",
          columnNames = {"provider", "external_id"}),
      @UniqueConstraint(name = "principal_user_id_uk", columnNames = "user_id")
    })
@NoArgsConstructor
@Getter
@Setter
public class PrincipalEntity {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Login of the principal. */
  @Column(length = 255, nullable = false)
  private String login;

  /** Authentication provider, for example CAS, OIDC, LDAP, or LOCAL. */
  @Column(length = 100, nullable = false)
  private String provider;

  /** Stable external identifier, for example OIDC sub or LDAP uid. */
  @Column(name = "external_id", length = 255, nullable = false)
  private String externalId;

  /** Business user identifier handled by the user domain/module. */
  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

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
      String login,
      String provider,
      String externalId,
      UUID userId,
      Set<StructureEntity> structureEntities) {
    this.id = id;
    this.login = login;
    this.provider = provider;
    this.externalId = externalId;
    this.userId = userId;
    this.structureEntities = structureEntities != null ? structureEntities : new HashSet<>();
  }

  public static PrincipalEntity of(
      UUID id,
      String login,
      String provider,
      String externalId,
      UUID userId,
      Set<StructureEntity> structureEntities) {
    return new PrincipalEntity(id, login, provider, externalId, userId, structureEntities);
  }

  public static PrincipalEntity of(
      UUID id, String login, String provider, String externalId, UUID userId) {
    return new PrincipalEntity(id, login, provider, externalId, userId, new HashSet<>());
  }
}
