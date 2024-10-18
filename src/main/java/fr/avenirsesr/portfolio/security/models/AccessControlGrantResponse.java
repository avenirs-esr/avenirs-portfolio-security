/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * <h1>AccessControlGrantResponse</h1>
 * <p>
 * <b>Description:</b> response to a grant operation.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 17/10/2024
 */

@Data()
@Accessors(chain=true)
@NoArgsConstructor
public class AccessControlGrantResponse {
	
	/** Login of the user. */
	private String login;

	/** Access granted or not. */
	private boolean granted;

	/** Message if an error occurs. */
	private String error;
}
