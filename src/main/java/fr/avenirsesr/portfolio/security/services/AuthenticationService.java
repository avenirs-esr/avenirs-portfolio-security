/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import fr.avenirsesr.portfolio.security.configuration.OIDCConfiguration;
import fr.avenirsesr.portfolio.security.models.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.models.OIDCProfileResponse;
import io.jsonwebtoken.Claims;

/**
 * Authentication service. Interact with theOIDC provider and generates URL
 * associated to OIDC end points.
 */
@Service
public class AuthenticationService {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

	/** Rest client to interact with OIDC provider. */
	private RestClient restClient = RestClient.create();

	/** OIDC settings */
	@Autowired
	private OIDCConfiguration oidcConfiguration;

	/** Template to generate the OIDC authorization URL. */
	@Value("${avenirs.authentication.oidc.authorise.template.url}")
	private String oidcAuthorizeTemplate;
	
	/** Template to generate the Access Token URL. */
	@Value("${avenirs.authentication.oidc.token.template.url}")
	private String oidcAccessTokenTemplate;

	@Value("${avenirs.authentication.oidc.provider.introspect.url}")
	private String oidcProviderIntrospectURL;

	/** Template to generate the service URL. */
	@Value("${avenirs.authentication.service.template}")
	private String serviceTemplate;

	/** Basic authentication header for the interaction with the OIDC provider. */
	private String basicAuthenticationHeader;

	/** Profile end point. */
	@Value("${avenirs.authentication.oidc.provider.profile.url}")
	private String oidcProviderProfileURL;
	
	@Autowired
	private JWTService jwtService; 

	/**
	 * Generates the OIDC Authorize URL.
	 * 
	 * @param host The host associated to the OIDC provider.
	 * @param code The code provided by the OIDC provider.
	 * @return The authorize URL.
	 * @throws IOException
	 */
	public String generateAuthorizeURL(String host, String code) throws IOException {

		String oidcAuthorizeURL = oidcAuthorizeTemplate.replaceAll("%HOST%", host).replaceAll("%CODE%", code)
				.replaceAll("%CLIENT_ID%", oidcConfiguration.getClientId())
				.replaceAll("%CLIENT_SECRET%", oidcConfiguration.getClientSecret());
		LOGGER.debug("generateAuthorizeURL, code: " + code);
		LOGGER.debug("generateAuthorizeURL, oidcAuthorizeURL: " + oidcAuthorizeURL);
		return oidcAuthorizeURL;
	}

	/**
	 * Generates the OIDC Access Token URL.
	 * @param login The user login.
	 * @param password The user password.
	 * @return The authorize URL.
	 * @throws IOException
	 */
	protected String generateAccessTokenURL(String login, String password) throws IOException {
		String oidcAccessTokenURL = oidcAccessTokenTemplate.replaceAll("%CLIENT_ID%", oidcConfiguration.getClientId())
				.replaceAll("%CLIENT_SECRET%", oidcConfiguration.getClientSecret())
				.replaceAll("%LOGIN%", login)
				.replaceAll("%PASSWORD%", password);
		LOGGER.debug("generateAccessTokenURL, oidcAccessTokenURL: " + oidcAccessTokenURL);
		return oidcAccessTokenURL;
	}

	
	/**
	 * Generates the OIDC Authorize URL.
	 * 
	 * @param host The host associated to the OIDC provider.
	 * @return The service URL.
	 * @throws IOException
	 */
	public String generateServiceURL(String host) throws IOException {

		String serviceURL = serviceTemplate.replaceAll("%HOST%", host);
		LOGGER.debug("generateServiceURL, serviceURL: " + serviceURL);
		return serviceURL;
	}

	/**
	 * Generates the OIDC Profile URL.
	 * 
	 * @param token The access token used to retrieve the user profile.
	 * @return The profile URL
	 * @throws IOException
	 */
	public String generateProfileURL(String token) {

		String profileURL = oidcProviderProfileURL + "?token=" + token;
		LOGGER.debug("generateProfileURL, profileURL: " + profileURL);
		return profileURL;
	}

	/**
	 * Generates the OIDC Introspect URL.
	 * 
	 * @param token The access token used to retrieve the user profile.
	 * @return The introspect URL
	 * @throws IOException
	 */
	public String generateIntrospectURL(String token) {

		String introspectURL = oidcProviderIntrospectURL + "?token=" + token;
		LOGGER.trace("generateIntrospectURL, introspectURL: " + introspectURL);
		return introspectURL;
	}

	/**
	 * Access token introspection end point.
	 * 
	 * @param token    The token to introspect.
	 * @param response The servlet response (used to redirect)
	 * @return The Profile response of the OIDC Provider.
	 * @throws IOException
	 */
	public OIDCProfileResponse profile(@RequestHeader(value = "x-authorization") String token) throws IOException {

		LOGGER.trace("profile");
		
		OIDCProfileResponse profileResponse = restClient.post().uri(generateProfileURL(token))
				.header("Authorization", basicAutentication()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON).retrieve().body(OIDCProfileResponse.class);

		LOGGER.warn("profile, profileResponse: " + profileResponse);

		return profileResponse;
	}

	/**
	 * OIDC introspection of an access token.
	 * 
	 * @param token The access token to introspect.
	 * @return The introspect response of the OIDS Provider.
	 */
	public OIDCIntrospectResponse introspectAccessToken(String token) {

		LOGGER.debug("introspectAccessToken");
		
		OIDCIntrospectResponse introspectResponse = restClient.post().uri(generateIntrospectURL(token))
				.header("Authorization", basicAutentication()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON).retrieve().body(OIDCIntrospectResponse.class);

		LOGGER.debug("introspect, introspectResponse: " + introspectResponse);

		return introspectResponse;
	}
	
	
	/**
	 * Generates an access token
	      
	 * @param login The user login.
	 * @param password The user password.
	 * @return An Optional of OIDCAccessTokenResponse
	 * @throws Exception
	 */
	 public Optional<OIDCAccessTokenResponse> getAccessToken(String login, String password) throws Exception {
		 try {

			 
	       String query = generateAccessTokenURL(login, password);
	       OIDCAccessTokenResponse response = restClient.post().uri(query)
	      			.retrieve().body(OIDCAccessTokenResponse.class);
	        
	        final Optional<Claims> claims = this.jwtService.parseAndCheckSignature(response);
	        if (claims.isEmpty()) {
	        	LOGGER.error("Invalid access token response: {}", response);
	        	return Optional.empty();
	        	
	        }
	        
	        LOGGER.trace("Claims: {}", claims.get());
	        return Optional.of(response);
	        
		 } catch(HttpClientErrorException e) {
			LOGGER.error("getAccessToken, error while retrieving access token for {}: {}", login, + e.getStatusCode().value());
			 return Optional.empty();
		 }
	    }

	/**
	 * Gives the header for a basic authentication based on the client id and client
	 * secret. The final header is generate only once and then reused.
	 * 
	 * @return The basic authentication header.
	 */
	public String basicAutentication() {
		if (basicAuthenticationHeader == null) {

			LOGGER.trace("basicAutentication generating authentication header. " );
			final String auth = oidcConfiguration.getClientId() + ":" + oidcConfiguration.getClientSecret();

			final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
			basicAuthenticationHeader = "Basic " + new String(encodedAuth);
		}
		LOGGER.trace("basicAuthentication, basicAuthenticationHeader: " + basicAuthenticationHeader);
		return basicAuthenticationHeader;
	}

}
