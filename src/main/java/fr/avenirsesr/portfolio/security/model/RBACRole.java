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

/** Role in the RBAC system. A role is associated to one or several permissions. */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "role")
public class RBACRole {

  /** Database id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name of the Role. */
  @Column(length = 80, nullable = false, unique = true)
  private String name;

  /** Descriptions of the Role. */
  @Column(length = 255, nullable = false)
  private String description;

  /** Permission associated to the role. */
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "id_role", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_permission", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "role_permission_pk",
              columnNames = {"id_role", "id_permission"}))
  private List<RBACPermission> permissions = new ArrayList<>();
}
