/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;


import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Assignment in the RBAC system.
 * An assignment links:
 * - an user (principal)
 * - a role, linked to one or many permissions.
 * - a scope which is liked to on or many resources.
 * - a context which determine additional information on the assignment, for instance a period of validity. 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(RBACAssignmentPK.class)
@Table(name="assignment")
public class RBACAssignment {
	/** Role in the assignment. */
	@Id
	@ManyToOne
	@JoinColumn(name="id_role", referencedColumnName = "id")
	private RBACRole role;

	/** Principal to which the role is assigned. */
	@Id
	@ManyToOne
	@JoinColumn(name="id_principal", referencedColumnName = "id")
	private Principal principal;
	
	/** The scope, which determine the resources involved in the assignment. */
	@Id
	@ManyToOne
	@JoinColumn(name="id_scope", referencedColumnName = "id")
	private RBACScope scope;
	
	/** The context, which determines some limits of the assignment. */
	@Id
	@ManyToOne
	@JoinColumn(name="id_context", referencedColumnName = "id")
	private RBACContext context;
	
	/** Date of the assignment. */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private LocalDateTime timestamp;

}
