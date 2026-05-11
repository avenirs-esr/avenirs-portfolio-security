package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Context in the RBAC system. A context is used to limit an assignment with a period of validity
 * and an establishment.
 */
@Data
@Accessors(chain = true)
@Entity
@Table(
    name = "context",
    indexes = {
      @Index(name = "context_validity_start_idx", columnList = "validity_start"),
      @Index(name = "context_validity_end_idx", columnList = "validity_end")
    })
public class RBACContextEntity {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Start of validity. */
  @Column(name = "validity_start", columnDefinition = "TIMESTAMP")
  private LocalDateTime validityStart;

  /** End of the validity. */
  @Column(name = "validity_end", columnDefinition = "TIMESTAMP")
  private LocalDateTime validityEnd;

  /** Effective date. */
  @Transient private LocalDateTime effectiveDate = LocalDateTime.now();

  /** Structures associated to the context. */
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "context_structure",
      joinColumns = @JoinColumn(name = "id_context", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_structure", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "context_structure_pk",
              columnNames = {"id_context", "id_structure"}))
  private Set<StructureEntity> structureEntities = new HashSet<>();
}
