/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
		
		return hasAccess(principal.getLogin(), action.getId(), resource.getId());
	}
	
	
	/**
	 * Checks that a principal has access to a resource to perform an action, without application context.
	 * @param login The login of the principal.
	 * @param actionId The id of the action to perform.
	 * @param resourceId The id of the accessed resource.
	 * @return True if the principal has access to the resource.
	 */
	boolean hasAccess(String login, Long actionId, Long resourceId) {
		LOGGER.trace("hasAccess, login: {}", login);
		LOGGER.trace("hasAccess, actionId: {}",  actionId);
		LOGGER.trace("hasAccess, resourceId: {}", resourceId);
		
		List<RBACPermission> requiredPermissions = this.fetchPermissions(actionId);
		LOGGER.trace("hasAccess, requiredPermissions: {}", requiredPermissions);
		
		if (requiredPermissions != null && requiredPermissions.size() > 0) {
			
			final List<RBACAssignment> principalAssignments = this.assignmentService.getAllAssignmentsByPredicate(RBACAssignmentSpecification.filterByPrincipalAndResources(login, resourceId));
			LOGGER.trace("hasAccess, principalAssignments: {}", principalAssignments);
			
			List<RBACPermission> principalPermissions = principalAssignments.stream().map(a -> { 
				 return a.getRole().getPermissions();
				 }).flatMap(Collection::stream).collect(Collectors.toList());
			LOGGER.trace("hasAccess, principalPermissions: {}", principalPermissions);
			
			final boolean accessGranted = principalPermissions.containsAll(requiredPermissions);
			LOGGER.trace("hasAccess, accessGranted: {}", accessGranted);
			
			return accessGranted;
		}
		return false;
	}

	/**
	 * Fetches the permissions associated to an action.
	 * @param actionName The name of the action.
	 * @return The permission associated to the action or null if the action is not found.
	 */
	private List<RBACPermission> fetchPermissions(String actionName){
		if (actionName != null && !this.permissionsByActionName.containsKey(actionName)) {
			
		RBACAction action = this.actionService.getAction(actionName).orElse(null);
		List<RBACPermission> permissions = action== null ? null : action.getPermissions();
		permissionsByActionName.put(actionName, permissions);
		}
		return permissionsByActionName.get(actionName);
		
	}
	
	/**
	 * Fetches the permissions associated to an action.
	 * @param actionName The name of the action.
	 * @return The permission associated to the action or null if the action is not found.
	 */
	private List<RBACPermission> fetchPermissions(Long actionId){
		
		Optional<RBACAction> action = this.actionService.getAction(actionId);
		
		String actionName  = action.isEmpty() ? "" : action.get().getName();
		return fetchPermissions(actionName);
		
		
	}
}
