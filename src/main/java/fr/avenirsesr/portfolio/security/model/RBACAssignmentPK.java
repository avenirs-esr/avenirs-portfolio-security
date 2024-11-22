/**
 * 
 */
package fr.avenirsesr.portfolio.security.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Assignment PrimaryKey.
 */
@Data
@Accessors(chain=true)
@NoArgsConstructor
@AllArgsConstructor
public class RBACAssignmentPK implements Serializable {

	@Serial
	private static final long serialVersionUID = -5520708663812175497L;

	/** Role id. */
	private UUID role;

	/** Principal id. */
	private UUID principal;

	/** Scope id. */
	private UUID scope;

	/** Context id. */
	private UUID context;

}
