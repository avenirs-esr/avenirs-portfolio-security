/**
 * 
 */
package fr.avenirsesr.portfolio.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;


/**
 * <h1>AccessControlRevokeResponse</h1>
 * <p>
 * <b>Description:</b> response to a revoke operation.
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
public class AccessControlRevokeResponse {
	
	/** Login of the user. */
	private String login;

	/** Access granted or not. */
	private boolean revoked;

	/** Message if an error occurs. */
	private String error;

	/** Assignment ID. */
	private UUID assignmentId;
}
