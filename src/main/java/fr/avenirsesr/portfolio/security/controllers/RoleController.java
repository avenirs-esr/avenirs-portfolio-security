package fr.avenirsesr.portfolio.security.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;

@RestController
public class RoleController {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
	
	/** Authentication service used to retrieve the user informations.*/
	@Autowired
	private AuthenticationService authenticationService; 
	
	@GetMapping("${avenirs.accessControl.roles}")
	public String getRoles(@RequestHeader(value="x-authorization") String token) {
		LOGGER.trace("getRoles, token: " + token);
		
		OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
		LOGGER.trace("getRoles, introspectResponse: " + introspectResponse);
		
		if (introspectResponse != null  && introspectResponse .isActive()) {
			String uid = introspectResponse.getUniqueSecurityName();
			LOGGER.trace("getRoles, uid: " + uid);
		}
		
		return  "getRoles: " + token;
	}
	
}
