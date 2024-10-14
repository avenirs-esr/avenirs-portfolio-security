/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.RBACContext;
import fr.avenirsesr.portfolio.security.models.RBACPermission;
import fr.avenirsesr.portfolio.security.models.RBACResource;
import fr.avenirsesr.portfolio.security.models.Structure;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecification;

/**
 * <h1>AccessControlService</h1>
 * <p>
 * <b>Description:</b> AccessControlService is used to check if a resource can be accessed by a principal.
 * It is an RBAC: Role Based Access Control.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 04/10/2024
 */
@Slf4j
@Service
public class AccessControlService {
	
	/** Action service */
	@Autowired
	RBACActionService actionService;

	/** Assignment service. */
	@Autowired
	RBACAssignmentService assignmentService;

	/** Cache for the permissions. */
	private final Map<String, List<RBACPermission>> permissionsByActionName = new HashMap<>();

	/**
	 * Checks that a principal has access to a resource to perform an action,
	 * without application context.
	 * 
	 * @param principal The principal.
	 * @param action    The action to perform.
	 * @param resource  The accessed resource.
	 * @return True if the principal has access to the resource.
	 */
	public boolean hasAccess(Principal principal, RBACAction action, RBACResource resource) {
		log.trace("hasAccess, principal: {}", principal);
		log.trace("hasAccess, action: {}", action);
		log.trace("hasAccess, resource: {}", resource);

		return hasAccess(principal.getLogin(), action.getId(), resource.getId());
	}

	/**
	 * Checks that a principal has access to a resource to perform an action,
	 * without application context.
	 * 
	 * @param login      The login of the principal.
	 * @param actionId   The id of the action to perform.
	 * @param resourceId The id of the accessed resource.
	 * @return True if the principal has access to the resource.
	 */
	public boolean hasAccess(String login, Long actionId, Long resourceId) {
		log.trace("hasAccess, login: {}", login);
		log.trace("hasAccess, actionId: {}", actionId);
		log.trace("hasAccess, resourceId: {}", resourceId);

		List<RBACPermission> requiredPermissions = this.fetchPermissions(actionId);
		log.trace("hasAccess, requiredPermissions: {}", requiredPermissions);

		return checkGrantedPermissions(requiredPermissions, login, resourceId);
	}

	/**
	 * Checks that a principal has access to a resource to perform an action,
	 * without application context.
	 * 
	 * @param login      The login of the principal.
	 * @param actionName The name of the action to perform.
	 * @param resourceId The id of the accessed resource.
	 * @return True if the principal has access to the resource.
	 */
	boolean hasAccess(String login, String actionName, Long resourceId) {
		log.trace("hasAccess, login: {}", login);
		log.trace("hasAccess, actionName: {}", actionName);
		log.trace("hasAccess, resourceId: {}", resourceId);

		List<RBACPermission> requiredPermissions = this.fetchPermissions(actionName);
		log.trace("hasAccess, requiredPermissions: {}", requiredPermissions);
		return checkGrantedPermissions(requiredPermissions, login, resourceId);
	}
	
	
	/**
	 * Checks the granted permission for a user regarding the required ones.
	 * @param requiredPermissions The required permissions.
	 * @param login The login of the user.
	 * @param resourceId The resource id.
	 * @return True if the permissions granted to the user for the resource contains all the required permissions.
	 */
	private boolean checkGrantedPermissions(List<RBACPermission> requiredPermissions, String login, Long resourceId) {
		if (requiredPermissions != null && !requiredPermissions.isEmpty()) {

			List<RBACAssignment> principalAssignments = this.assignmentService.getAllAssignmentsBySpecification(
					RBACAssignmentSpecification.filterByPrincipalAndResources(login, resourceId));
			log.trace("hasAccess, principalAssignments: {}", principalAssignments);
			
			List<RBACAssignment> validAssignment = filterByApplicationContext(principalAssignments);
			log.trace("hasAccess, validAssignment: {}", validAssignment);

			HashSet<RBACPermission> principalPermissions = validAssignment.stream().map(a -> {
				return a.getRole().getPermissions();
			}).flatMap(Collection::stream).collect(Collectors.toCollection(HashSet::new));
			log.trace("hasAccess, principalPermissions: {}", principalPermissions);

			boolean accessGranted = principalPermissions.containsAll(requiredPermissions);
			log.trace("hasAccess, accessGranted: {}", accessGranted);

			return accessGranted;
		}
		return false;
	}
	
	private List<RBACAssignment> filterByApplicationContext(List<RBACAssignment> principalAssignments) {
	  log.trace("filterByApplicationContext, principalAssignments: {}", principalAssignments);
	  
	  if (CollectionUtils.isEmpty(principalAssignments)) {
	    return new ArrayList<RBACAssignment>();
	  }
	    Principal principal = principalAssignments.getFirst().getPrincipal();
	    RBACContext executionContext = createExecutionContext(principal);
	    return principalAssignments.stream()
	        .filter(a -> checkExecutionContext(a.getContext(), executionContext))
	        .collect(Collectors.toList());
	}
	
	/**
	 * Creates an execution context.
	 * @param principal The Principal associated to the execution context.
	 * @return The execution context.
	 */
	public RBACContext createExecutionContext(Principal principal) { // Need to be public to be mocked in tests.
	  return new RBACContext().setStructures(principal.getStructures());
	}
	
	private boolean checkExecutionContext(RBACContext applicationContext, RBACContext executionContext) {
	  log.trace("checkExecutionContext, applicationContext: {}", applicationContext);
	  log.trace("checkExecutionContext, executionContext: {}", executionContext);
      
	  if (applicationContext != null) {
	    LocalDateTime validityStart = applicationContext.getValidityStart();
	    LocalDateTime validityEnd= applicationContext.getValidityEnd();
	    
	    log.trace("checkExecutionContext, validityStart: {}", validityStart);
	    log.trace("checkExecutionContext, validityEnd: {}", validityEnd);
	    log.trace("checkExecutionContext, executionContext.getEffectiveDate(): {}", executionContext.getEffectiveDate());
	    
	    if (validityStart != null && executionContext.getEffectiveDate().isBefore(validityStart)) {
	      log.trace("checkExecutionContext, validityStart not respected");
	        return false;
	    }
	    
	    if (validityEnd != null && executionContext.getEffectiveDate().isAfter(validityEnd)) {
	      log.trace("checkExecutionContext, validityEnd not respected");
            return false;
          }
	  Set<Structure> exContextStructures = executionContext.getStructures();
	  Set<Structure> appContextStructures = applicationContext.getStructures();
	  log.trace("checkExecutionContext, exContextStructures: {}", exContextStructures);
	  log.trace("checkExecutionContext, appContextStructures: {}", appContextStructures);
	  
	  if (CollectionUtils.isEmpty(appContextStructures)) {
	    log.trace("checkExecutionContext, no structure in application context");
	    return true;
	  }
	  
	  if (CollectionUtils.isEmpty(exContextStructures) || !exContextStructures.containsAll(appContextStructures)) {
	    log.trace("checkExecutionContext, structures of application context not respected");
	    return false;
	  }
	  
	  log.trace("checkExecutionContext, structures of application context respected");
	  return true;
	  }
	  
	  return true;
	}
	

	/**
	 * Fetches the permissions associated to an action.
	 * 
	 * @param actionName The name of the action.
	 * @return The permission associated to the action or null if the action is not
	 *         found.
	 */
	private List<RBACPermission> fetchPermissions(String actionName) {
		if (actionName != null && !this.permissionsByActionName.containsKey(actionName)) {

			RBACAction action = this.actionService.getActionByName(actionName).orElse(null);
			List<RBACPermission> permissions = action == null ? null : action.getPermissions();
			permissionsByActionName.put(actionName, permissions);
		}
		return permissionsByActionName.get(actionName);

	}

	/**
	 * Fetches the permissions associated to an action.
	 * 
	 * @param actionId The id of the action.
	 * @return The permission associated to the action or null if the action is not
	 *         found.
	 */
	private List<RBACPermission> fetchPermissions(Long actionId) {

		Optional<RBACAction> action = this.actionService.getActionById(actionId);

		String actionName = action.isEmpty() ? "" : action.get().getName();
		return fetchPermissions(actionName);

	}
}
