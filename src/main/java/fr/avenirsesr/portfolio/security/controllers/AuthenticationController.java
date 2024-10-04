package fr.avenirsesr.portfolio.security.controllers;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.models.OIDCProfileResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authentication Controller.
 * Interact with an OIDC provider, Apereo CAS for instance.
 * Can retrieve/Validate/introspect an access token.
 */
@RestController
public class AuthenticationController {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
	
	/** Authentication service. */
	@Autowired 
	private AuthenticationService authenticationService;
	
	/**
	 * Callback after OIDC authentication.
	 * @param forwardHost The header used to retrieve the current host. This is used to determine the end point from the current request. 
	 * @param response The response instance used to redirect to the authorize end point.
	 * @param code The session code used to issue an access token.
	 * @throws IOException If an input or output exception occurs.
	 */
	@GetMapping("${avenirs.authentication.oidc.callback}")
	public void oidcCallback(@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response,
			@RequestParam Optional<String> code) throws IOException {
		LOGGER.trace("oidcCallback");
		response.sendRedirect(this.authenticationService.generateAuthorizeURL(forwardHost.orElse("localhost"), code.orElse("NO_PROVIDED_CODE")));
	}
		
	/**
	 * Performs the redirection after the access token is retrieved.
	 *
	 * @param host     The header used to determine the host.
	 * @param response The servlet response instance, used to perform a redirection.
	 * @throws IOException If an input or output exception occurs.
	 */
	@GetMapping("${avenirs.authentication.oidc.callback.redirect}")
	
	public void redirect(@RequestHeader(value="x-forwarded-host") Optional<String> host,
			HttpServletResponse response) throws IOException{
		LOGGER.trace("redirect");
		response.sendRedirect(this.authenticationService.generateServiceURL(host.orElse("localhost")));
	}
	
	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @return The response of the OIDC provider.
	 * @throws IOException  If an input or output exception occurs.
	 */
	@PostMapping("${avenirs.authentication.oidc.callback.profile}")
	public OIDCProfileResponse profile(@RequestHeader(value="x-authorization") String token) throws IOException{
		OIDCIntrospectResponse introspectResponse = this.authenticationService.introspectAccessToken(token);
		
		if (introspectResponse != null && introspectResponse.isActive()) {
			OIDCProfileResponse profileResponse = this.authenticationService.profile(token);
				return profileResponse;
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}
		
	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @return The OIDC Provider response.
	 * @throws IOException
	 */
	@PostMapping("${avenirs.authentication.oidc.callback.introspect}")
	public OIDCIntrospectResponse introspect(@RequestHeader(value="x-authorization") String token) throws IOException{
		return this.authenticationService.introspectAccessToken(token);
	}
}
