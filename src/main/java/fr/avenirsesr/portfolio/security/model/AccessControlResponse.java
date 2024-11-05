/**
 * 
 */
package fr.avenirsesr.portfolio.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;


/**
 * Access control response
 */
@Data()
@Accessors(chain=true)
@NoArgsConstructor
public class AccessControlResponse {
	
	/** Login of the user. */
	private String login;
	
	
	/** Name of the action. */
	private String actionName;
	
	/** ID of the resource. */
	private UUID resourceId;
	
	/** Access granted or not. */
	private boolean granted;
	
	/** Uri */
	private String uri;
	
	/** HTTP method. */
	private String method;
			
}
