/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.RBACPermission;
import fr.avenirsesr.portfolio.security.models.RBACResource;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecification;

/**
 * Access Control Service
 */
@Service
public class AccessControlService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlService.class);
	
	/** Action service*/
	@Autowired
	RBACActionService actionService;
	
	/** Assignment service. */
	@Autowired 
	RBACAssignmentService assignmentService;
	
	/** Cache for the permissions. */
	private Map<String,List<RBACPermission>> permissionsByActionName = new HashMap<>();
	
	/**
	 * Checks that a principal has access to a resource to perform an action, without application context.
	 * @param principal The principal.
	 * @param action The action to perform.
	 * @param resource The accessed resource.
	 * @return True if the principal has access to the resource.
	 */
	boolean hasAccess(Principal principal, RBACAction action, RBACResource resource) {
		LOGGER.trace("hasAccess, principal: {}", principal);
		LOGGER.trace("hasAccess, action: {}",  action);
		LOGGER.trace("hasAccess, resource: {}", resource);
		
		List<RBACPermission> requiredPermissions = this.fetchPermissions(action.getName());
		if (requiredPermissions != null) {
			final List<RBACAssignment> principalAssignments = this.assignmentService.getAllAssignmentsByPredicate(RBACAssignmentSpecification.filterByPrincipal(principal.getLogin()));
			LOGGER.trace("hasAccess, principalAssignments: {}", principalAssignments);
			
			
		}
		return false;
	}

	/**
	 * Fetch the permissions associated to an action.
	 * @param actionName The name of the action.
	 * @return The permission associated to the action or null if the action is not found.
	 */
	List<RBACPermission> fetchPermissions(String actionName){
		if (!this.permissionsByActionName.containsKey(actionName)) {
			
		RBACAction action = this.actionService.getAction(actionName).orElse(null);
		List<RBACPermission> permissions = action== null ? null : action.getPermissions();
		permissionsByActionName.put(actionName, permissions);
		}
		return permissionsByActionName.get(actionName);
		
	}
}
