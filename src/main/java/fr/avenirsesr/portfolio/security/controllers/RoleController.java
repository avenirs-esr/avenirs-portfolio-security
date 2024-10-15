package fr.avenirsesr.portfolio.security.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;

@Slf4j
@RestController
public class RoleController {
	
	/** Authentication service used to retrieve the user information.*/
	@Autowired
	private AuthenticationService authenticationService; 
	
	@GetMapping("${avenirs.access.control.roles}")
	public String getRoles(@RequestHeader(value="x-authorization") String token) {
		log.trace("getRoles, token: {}", token);
		
		OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
		log.trace("getRoles, introspectResponse: {}", introspectResponse);
		
		if (introspectResponse != null  && introspectResponse .isActive()) {
			String uid = introspectResponse.getUniqueSecurityName();
			log.trace("getRoles, uid: {}", uid);
		}
		
		return  "getRoles: " + token;
	}
	
}
