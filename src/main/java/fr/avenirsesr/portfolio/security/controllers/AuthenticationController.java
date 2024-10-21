package fr.avenirsesr.portfolio.security.controllers;

import java.io.IOException;

import fr.avenirsesr.portfolio.security.models.LoginRequest;
import fr.avenirsesr.portfolio.security.models.OIDCAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

@Slf4j
@RestController
public class AuthenticationController {
	/** Constant for a missing authorization code. */
	public static final String NO_PROVIDED_CODE = "NO_PROVIDED_CODE";

	/** Authentication service. */
	@Autowired 
	private AuthenticationService authenticationService;


	/**
	 * Gives an access token.
	 * @param request The object with the credentials.
	 * @return The access token.
	 * @throws IOException If the access token could not be retrieved.
	 */
	@SuppressWarnings("SpringOmittedPathVariableParameterInspection")
	@PostMapping("${avenirs.authentication.oidc.login}")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) throws IOException {
		log.trace("login, request: {}", request);
		OIDCAccessTokenResponse response = authenticationService.getAccessToken(request.getLogin(), request.getPassword())
				.orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials"));
		return ResponseEntity.ok(response.getAccessToken());

	}
	
	/**
	 * Callback after OIDC authentication.
	 * @param forwardHost The header used to retrieve the current host. This is used to determine the end point from the current request. 
	 * @param response The response instance used to redirect to the authorize end point.
	 * @param code The session code used to issue an access token.
	 * @throws IOException If an input or output exception occurs.
	 */
	@SuppressWarnings("SpringOmittedPathVariableParameterInspection")
	@GetMapping("${avenirs.authentication.oidc.callback}")
	public void oidcCallback(@RequestHeader(value="x-forwarded-host", required=false) String forwardHost,
			HttpServletResponse response,
			@RequestParam(value="code", required=false) String code) throws IOException {
		log.trace("oidcCallback");
		response.sendRedirect(this.authenticationService.generateAuthorizeURL(forwardHost == null ? "localhost":forwardHost,
				code == null ? NO_PROVIDED_CODE : code));
	}

	/**
	 * Performs the redirection after the access token is retrieved.
	 *
	 * @param host     The header used to determine the host.
	 * @param response The servlet response instance, used to perform a redirection.
	 * @throws IOException If an input or output exception occurs.
	 */
	@SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @GetMapping("${avenirs.authentication.oidc.callback.redirect}")
	
	public void redirect(@RequestHeader(value="x-forwarded-host", required=false) String host,
			HttpServletResponse response) throws IOException{
		log.trace("redirect");
		response.sendRedirect(this.authenticationService.generateServiceURL(host == null ? "localhost": host));
	}
	
	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @return The response of the OIDC provider.
	 */
	@SuppressWarnings("SpringOmittedPathVariableParameterInspection")
	@PostMapping("${avenirs.authentication.oidc.callback.profile}")
	public OIDCProfileResponse profile(@RequestHeader(value="x-authorization") String token) {
		OIDCIntrospectResponse introspectResponse = this.authenticationService.introspectAccessToken(token);
		
		if (introspectResponse != null && introspectResponse.isActive()) {
            return this.authenticationService.profile(token);
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}

	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @return The OIDC Provider response.
	 */
	@SuppressWarnings("SpringOmittedPathVariableParameterInspection")
	@PostMapping("${avenirs.authentication.oidc.callback.introspect}")
	public OIDCIntrospectResponse introspect(@RequestHeader(value="x-authorization") String token) {
		return this.authenticationService.introspectAccessToken(token);
	}
}
