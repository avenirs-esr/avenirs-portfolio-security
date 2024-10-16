package fr.avenirsesr.portfolio.security.controllers;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.RBACRole;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecificationHelper;
import fr.avenirsesr.portfolio.security.services.RBACAssignmentService;
import fr.avenirsesr.portfolio.security.services.RBACRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class RoleController {
	
	/** Authentication service used to retrieve the user information.*/
	@Autowired
	private AuthenticationService authenticationService;

	/** Assignment service, used to retrieve roles assigned to a principal for instance.*/
	@Autowired
	private RBACAssignmentService assignmentService;
	
	@GetMapping("/roles")
	public List<String> getRoles(@RequestHeader(value="x-authorization") String token) {
		log.trace("getRoles, token: {}", token);
		
		OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
		log.trace("getRoles, introspectResponse: {}", introspectResponse);
		
		if (introspectResponse != null  && introspectResponse .isActive()) {
			String uid = introspectResponse.getUniqueSecurityName();
			log.trace("getRoles, uid: {}", uid);
			List<RBACAssignment> assignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipal(uid));

			List<String> roles = assignments.stream()
					.map(assignment->assignment.getRole().getName())
					.toList();
			log.trace("Role for {}: {}", uid, roles);
			return roles;
		}
		
		return Collections.emptyList();
	}
	
}
