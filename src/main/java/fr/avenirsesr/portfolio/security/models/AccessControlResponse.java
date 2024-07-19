/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * Access control response
 */
@Data()
@Accessors(chain=true)
@NoArgsConstructor
public class AccessControlResponse {
	
	/** Access token. */
	private String token;
	
	
	/** Login of the user. */
	private String login;
	
	
	/** Name of the action. */
	private String actionName;
	
	/** Id of the resource. */
	private Long resourceId;
	
	/** Access granted or not. */
	private boolean granted;
	
	/** Uri */
	private String uri;
	
	/** HTTP method. */
	private String method;
	
		
}
