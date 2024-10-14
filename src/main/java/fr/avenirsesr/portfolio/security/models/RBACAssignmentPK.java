/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;

import java.io.Serializable;

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
	private Long role;

	/** Principal id. */
	private Long principal;

	/** Scope id. */
	private Long scope;

	/** Context id. */
	private Long context;

}
