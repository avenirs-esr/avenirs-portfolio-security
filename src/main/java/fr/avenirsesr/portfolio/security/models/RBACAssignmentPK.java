/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;

import java.io.Serializable;

import lombok.Data;

/**
 * Assignment PrimaryKey.
 */
@Data
public class RBACAssignmentPK implements Serializable {
	
	
	private static final long serialVersionUID = 1951400835037328991L;

	/** Role id. */
	private Long role;

	/** Principal id. */
	private Long principal;

	/** Context id. */
	private Long context;

	/** Scope id. */
	private Long scope;

	/**
	 * Default constructor.
	 */
	public RBACAssignmentPK() {
	}

	/**
	 * Constructor.
	 * @param role The id of the role.
	 * @param principal The id of the  principal.
	 * @param context The id of the context.
	 * @param scope The id of the scope.
	 */
	public RBACAssignmentPK(Long role, Long principal,  Long context, Long scope) {
		this.role = role;
		this.principal = principal;
		this.context = context;
		this.scope = scope;
	}
}
