/**
 * 
 */
package fr.avenirsesr.portfolio.security.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * <h1>RBACAssignment</h1>
 * <p>
 * <b>Description:</b> represents an assignment in the RBAC system<br/>  
 * An assignment links:<br/>
 * <ul>
 * <li> an user (principal)</li>
 * <li> a role, linked to one or many permissions.</li>
 * <li> a scope which is liked to on or many resources.</li>
 * <li> a context which determine additional information on the assignment, for instance a period of validity.</li> 
 * </ul> 
 * </p>
 * 
 * <h2>Version:</h2>
 * 1.0
 * 
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 16 Sept 2024
 */
@Data
@Accessors(chain=true)
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
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_scope", referencedColumnName = "id")
	private RBACScope scope;
	
	/** The context, which determines some limits of the assignment. */
	@Id
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_context", referencedColumnName = "id")
	private RBACContext context;
	
	/** Date of the assignment. */
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private LocalDateTime timestamp;


	public RBACAssignmentPK getId() {
		return new RBACAssignmentPK(this.role.getId(), this.principal.getId(), this.scope.getId(), this.context.getId());
	}

}
