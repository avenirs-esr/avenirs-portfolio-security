/** */
package fr.avenirsesr.portfolio.security.model;

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
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/** Principal in the RBAC system. */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "principal",
    indexes = {@Index(name = "principal_login_idx", columnList = "login")})
public class Principal {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Login of the user. */
  @Column(length = 255, nullable = false, unique = true)
  private String login;

  /** Structures associated to the principal. */
  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "principal_structure",
      joinColumns = @JoinColumn(name = "id_principal", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "id_structure", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "principal_structure_pk",
              columnNames = {"id_principal", "id_structure"}))
  private Set<Structure> structures = new HashSet<>();
}
