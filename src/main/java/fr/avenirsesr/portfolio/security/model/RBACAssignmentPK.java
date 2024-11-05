/**
 * 
 */
package fr.avenirsesr.portfolio.security.model;

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

	/** Role id. */
	private UUID role;

	/** Principal id. */
	private UUID principal;

	/** Scope id. */
	private UUID scope;

	/** Context id. */
	private UUID context;

}
