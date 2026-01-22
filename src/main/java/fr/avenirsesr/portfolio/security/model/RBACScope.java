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
 * Scope in the RBAC system. A scope is used to determine the resources involved in a Role
 * assignment.
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "scope")
public class RBACScope {

  /** Database id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name of the scope. */
  @Column(length = 255)
  private String name;

  /** Resources associated to the scope. */
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "scope_resource",
      joinColumns = @JoinColumn(name = "id_scope", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_resource", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "scope_resource_pk",
              columnNames = {"id_scope", "id_resource"}))
  private List<RBACResource> resources = new ArrayList<>();
}
