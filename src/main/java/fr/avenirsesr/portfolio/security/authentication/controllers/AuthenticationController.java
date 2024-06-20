package fr.avenirsesr.portfolio.security.authentication.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.avenirsesr.portfolio.security.configuration.OIDCConfiguration;
import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.models.OIDCProfileResponse;
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
	
	@Autowired
	private OIDCConfiguration oidcConfiguration ;
	
	private String authenticationHeader;
	
	@Value("${avenirs.authentication.oidc.authorise.template.url}")
	private String oidcAuthorizeTemplate;
	
	@Value("${avenirs.authentication.oidc.provider.introspect.url}")
	private String oidcProviderIntrospectURL;
		
	@Value("${avenirs.authentication.oidc.provider.profile.url}")
	private String oidcProviderProfileURL;
	
	
	/** Rest client to interact with OIDC provider. */
	private RestClient restClient = RestClient.create();
	
	/**
	 * Callback after OIDC authentication.
	 * @param forwardHost The header used to retrieve the current host. This is used to determine the end point from the current request. 
	 * @param response The response instance used to redirect to the authorize end point.
	 * @param code The session code used to issue an access token.
	 * @throws IOException
	 */
	@GetMapping("${avenirs.authentication.oidc.callback}")
	public void oidcCallback(@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response,
			@RequestParam("code") Optional<String> code) throws IOException {
		String host = forwardHost.isEmpty() ?  "localhost" : forwardHost.get();
		String oidcAuthorizeURL = oidcAuthorizeTemplate
				.replaceAll("%HOST%", host)
				.replaceAll("%CODE%", code.orElse("NO_PROVIDED_CODE"))
				.replaceAll("%CLIENT_ID%", oidcConfiguration.getClientId())
				.replaceAll("%CLIENT_SECRET%", oidcConfiguration.getClientSecret());
		LOGGER.debug("oidcCallback code: " + code);
		LOGGER.debug("oidcCallback oidcAuthorizeURL: " + oidcAuthorizeURL);
		response.sendRedirect(oidcAuthorizeURL);
	}
	
	
	/**
	 * Perform the redirection after the access token is retrieved.
	 * @param forwardHost Header to determine the host.
	 * @param response The servlet response instance, used to perform a redirection.
	 * @throws IOException
	 */
	@GetMapping("${avenirs.authentication.oidc.callback.redirect}")
	public void redirect(@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response) throws IOException{
		String host = forwardHost.isEmpty() ?  "localhost" : forwardHost.get();
		LOGGER.debug("redirect");
		response.sendRedirect("http://" + host + "/examples/retrieve-access-token.html");
	}
	
	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @param response The servlet response (used to redirect)
	 * @return
	 * @throws IOException
	 */
	@PostMapping("${avenirs.authentication.oidc.callback.profile}")
	public OIDCProfileResponse profile(@RequestHeader(value="x-authorization") String token) throws IOException{
		OIDCIntrospectResponse introspectResponse = introspectAccessToken(token);
		
		if (introspectResponse != null && introspectResponse.isActive()) {
			OIDCProfileResponse profileResponse = restClient.post()
				.uri(oidcProviderProfileURL+"?token="+token)
				.header("Authorization", basicAutentication())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(OIDCProfileResponse.class);
			
				LOGGER.warn("profile, profileResponse: " +  profileResponse);
				profileResponse.setActive(true);
				return profileResponse;
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}
		
	/**
	 * Access token introspection end point. 
	 * @param token The token to introspect.
	 * @param response The servlet response (used to redirect)
	 * @return
	 * @throws IOException
	 */
	@PostMapping("${avenirs.authentication.oidc.callback.introspect}")
	public String introspect(@RequestHeader(value="x-authorization") String token) throws IOException{
		OIDCIntrospectResponse introspectResponse = introspectAccessToken(token);
		return introspectResponse.toString();
	}
	
	/**
	 * Internal method for OIDC introspection of an access token.
	 * @param forwardedHost The host to use for the introspection.
	 * @param token The access token to introspect.
	 * @return
	 */
	private OIDCIntrospectResponse introspectAccessToken(String token) {
		
		LOGGER.debug("introspect, introspectURL: " + oidcProviderIntrospectURL);
		
		OIDCIntrospectResponse introspectResponse = restClient.post()
				.uri(oidcProviderIntrospectURL+"?token="+token)
				.header("Authorization", basicAutentication())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(OIDCIntrospectResponse.class);
			
				LOGGER.warn("introspect, introspectResponse: " +  introspectResponse);
				
		return introspectResponse;
	}
	
	/**
	 * Gives the header for a basic authentication based on the client id and client secret.
	 * The final header is generate only once and then reused.
	 * @return The basic authentication header.
	 */
	public String basicAutentication() {
		if (authenticationHeader == null) {
			
			final String auth = oidcConfiguration.getClientId() + ":" + oidcConfiguration.getClientSecret();
			LOGGER.debug("validate auth: " + auth);
		
			final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
	        authenticationHeader = "Basic " + new String( encodedAuth );
	    }
		return authenticationHeader;
	}
}
