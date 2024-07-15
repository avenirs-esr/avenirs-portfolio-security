package fr.avenirsesr.portfolio.security.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AccessControlService;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;

@RestController
public class AccessControlController {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlController.class);
	
	/** Authentication service used to retrieve the user informations.*/
	@Autowired
	private AuthenticationService authenticationService; 
	
	
	/** Access control service. */
	@Autowired
	private AccessControlService accessControlService;
	
	@GetMapping("${avenirs.accessControl}")
	public Boolean hasAccess(@RequestHeader(value="x-authorization") String token, String uri, String method) {
		LOGGER.trace("hasAccess, token: {} ", token);
		LOGGER.trace("hasAccess, uri: {} ", uri);
		LOGGER.trace("hasAccess, method: {} ", method);
		
		OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
		LOGGER.trace("hasAccess, introspectResponse: " + introspectResponse);
		
		if (introspectResponse != null  && introspectResponse .isActive()) {
			String uid = introspectResponse.getUniqueSecurityName();
			LOGGER.trace("getRoles, uid: " + uid);
		}
		
		
		return  false;
	}
	
}
