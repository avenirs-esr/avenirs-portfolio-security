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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



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
