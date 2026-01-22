/** */
package fr.avenirsesr.portfolio.security.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 *
 * <h1>RBACAssignment</h1>
 *
 * <p><b>Description:</b> represents an assignment in the RBAC system<br>
 * An assignment links:<br>
 *
 * <ul>
 *   <li>an user (principal)
 *   <li>a role, linked to one or many permissions.
 *   <li>a scope which is liked to on or many resources.
 *   <li>a context which determine additional information on the assignment, for instance a period
 *       of validity.
 * </ul>
 *
 * <h2>Version:</h2>
 *
 * 1.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 16 Sept 2024
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(
    name = "assignment",
    uniqueConstraints =
        @UniqueConstraint(
            name = "unique_assignment_constraint",
            columnNames = {"id_role", "id_principal", "id_scope", "id_context"}))
public class RBACAssignment {
  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Role in the assignment. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "id_role", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RBACRole role;

  /** Principal to which the role is assigned. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "id_principal", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Principal principal;

  /** The scope, which determines the resources involved in the assignment. */
  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "id_scope", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RBACScope scope;

  /** The context, which determines some limits of the assignment. */
  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "id_context", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RBACContext context;

  /** Date of the assignment. */
  @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
  private LocalDateTime timestamp;
}
