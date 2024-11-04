/**
 * 
 */
package fr.avenirsesr.portfolio.security.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import fr.avenirsesr.portfolio.security.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.model.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.model.OIDCProfileResponse;
import io.jsonwebtoken.Claims;

/**
 * <h1>AuthenticationService</h1>
 * <p>
 * <b>Description:</b> Interacts with OIDC Provider to retrieve or check access token (JWT)
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 04/10/2024
 */
@Slf4j
@Service
public class AuthenticationService {
	
	/** Rest client to interact with OIDC provider. */
	private final RestClient restClient = RestClient.create();

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

	@Value("${avenirs.authentication.oidc.client.id}")
	private String clientId;

	@Value("${avenirs.authentication.oidc.client.secret}")
	private String clientSecret;

	@Autowired
	private JWTService jwtService;

	/**
	 * Generates the OIDC Authorize URL.
	 * 
	 * @param host The host associated to the OIDC provider.
	 * @param code The code provided by the OIDC provider.
	 * @return The authorize URL.
	 */
	public String generateAuthorizeURL(String host, String code) {

		String oidcAuthorizeURL = String.format(oidcAuthorizeTemplate, host, host, code);
		log.trace("generateAuthorizeURL, host: {}", host);
		log.trace("generateAuthorizeURL, code: {}", code);
		log.debug("generateAuthorizeURL, oidcAuthorizeURL: {}", oidcAuthorizeURL);
		return oidcAuthorizeURL;
	}

	/**
	 * Generates the OIDC Access Token URL.
	 * 
	 * @param login    The user login.
	 * @param password The user password.
	 * @return The authorize URL.
	  */
	protected String generateAccessTokenURL(String login, String password) {
		String oidcAccessTokenURL = String.format(oidcAccessTokenTemplate, login, password);

		if (log.isDebugEnabled()) {
			String maskedPassword = "*".repeat(password.length());
			String maskedOIDCAccessTokenURL = String.format(oidcAccessTokenTemplate, login, maskedPassword);
			log.debug("generateAccessTokenURL, maskedOIDCAccessTokenURL: {}", maskedOIDCAccessTokenURL);
		}
		return oidcAccessTokenURL;
	}

	/**
	 * Generates the OIDC Authorize URL.
	 * 
	 * @param host The host associated to the OIDC provider.
	 * @return The service URL.
	  */
	public String generateServiceURL(String host) {

		String serviceURL = String.format(serviceTemplate, host);
		log.debug("generateServiceURL, serviceURL: {}", serviceURL);
		return serviceURL;
	}

	/**
	 * Generates the OIDC Profile URL.
	 * 
	 * @param token The access token used to retrieve the user profile.
	 * @return The profile URL
	 */
	public String generateProfileURL(String token) {

		String profileURL = String.format(oidcProviderProfileURL, token);
		if (log.isDebugEnabled()) {
			String maskedProfileURL = String.format(oidcProviderProfileURL, token.substring(0, 4) + "****" + token.substring(token.length() - 4));
			log.debug("generateProfileURL, maskedProfileURL: {}", maskedProfileURL);
		}
		return profileURL;
	}

	/**
	 * Generates the OIDC Introspect URL.
	 * 
	 * @param token The access token used to retrieve the user profile.
	 * @return The introspect URL
	 */
	public String generateIntrospectURL(String token) {

		String introspectURL = String.format(oidcProviderIntrospectURL, token);
		if (log.isDebugEnabled()) {
			String maskedIntrospectURL = String.format(oidcProviderIntrospectURL, token.substring(0, 4) + "****" + token.substring(token.length() - 4));
			log.debug("generateProfileURL, maskedIntrospectURL: {}", maskedIntrospectURL);
		}

		log.trace("generateIntrospectURL, introspectURL: {}", introspectURL);
		return introspectURL;
	}

	/**
	 * Gets the profile associated to an access token.
	 * 
	 * @param token The token granted to the user for which the profile has to be retrieved.
	 * @return The Profile response of the OIDC Provider.
	 */
	public OIDCProfileResponse profile(String token) {

		log.trace("profile");

OIDCProfileResponse profileResponse = restClient.post()
				.uri(generateProfileURL(token))
				.header("Authorization", basicAuthentication())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(OIDCProfileResponse.class);

		log.debug("profile, profileResponse: {}", profileResponse);

		return profileResponse;
	}

	/**
	 * OIDC introspection of an access token.
	 * 
	 * @param token The access token to introspect.
	 * @return The introspect response of the OIDC Provider.
	 */
	public OIDCIntrospectResponse introspectAccessToken(String token) {

		log.debug("introspectAccessToken");

		OIDCIntrospectResponse introspectResponse = restClient
				.post()
				.uri(generateIntrospectURL(token))
				.header("Authorization", basicAuthentication())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(OIDCIntrospectResponse.class);

		log.debug("introspect, introspectResponse: {}", introspectResponse);

		return introspectResponse;
	}

	/**
	 * Generates an access token
	 * 
	 * @param login    The user login.
	 * @param password The user password.
	 * @return An Optional of OIDCAccessTokenResponse
	 */
	public Optional<OIDCAccessTokenResponse> getAccessToken(String login, String password) {
		try {

			String query = generateAccessTokenURL(login, password);
			OIDCAccessTokenResponse response = restClient.post().uri(query).retrieve()
					.body(OIDCAccessTokenResponse.class);

			if (response == null) {
				return Optional.empty();
			}

			final Optional<Claims> claims = this.jwtService.parseAndCheckSignature(response);
			if (claims.isEmpty()) {
				log.error("Invalid access token response: {}", response);
				return Optional.empty();
			}
			
			log.trace("Claims: {}", claims.get());
			response.setClaims(new HashMap<>(claims.get()));
			return Optional.of(response);
		} catch (HttpClientErrorException e) {
			log.error("getAccessToken, error while retrieving access token for {}: {}", login,
					+e.getStatusCode().value());
			return Optional.empty();
		}
	}

	/**
	 * Gives the header for a basic authentication based on the client id and client
	 * secret. The final header is generated only once and then reused.
	 * 
	 * @return The basic authentication header.
	 */
	private String basicAuthentication() {
		if (basicAuthenticationHeader == null) {

			log.trace("basicAuthentication generating authentication header. ");
			final String auth = clientId + ":" + clientSecret;

			final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
			basicAuthenticationHeader = "Basic " + new String(encodedAuth);
		}
		log.trace("basicAuthentication, basicAuthenticationHeader: {}", basicAuthenticationHeader);
		return basicAuthenticationHeader;
	}

}
