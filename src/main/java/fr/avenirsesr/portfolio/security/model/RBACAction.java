package fr.avenirsesr.portfolio.security.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "action")
public class RBACAction {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name associated to the context. */
  @Column(length = 255, nullable = false, unique = true)
  private String name;

  /** Description of the context. */
  @Column(length = 255, nullable = false)
  private String description;

  /** Permissions associated to the scope. */
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "action_permission",
      joinColumns = @JoinColumn(name = "id_action", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_permission", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "action_permission_pk",
              columnNames = {"id_action", "id_permission"}))
  private List<RBACPermission> permissions = new ArrayList<>();
}
