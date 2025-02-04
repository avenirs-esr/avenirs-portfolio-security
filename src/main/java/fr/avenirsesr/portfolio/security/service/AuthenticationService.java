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

	/** Template to generate the Access Token query body. */
	@Value("${avenirs.authentication.oidc.token.template.body}")
	private String oidcAccessTokenBodyTemplate;

	/** Template to generate the Access Token query body. */
	@Value("${avenirs.authentication.oidc.code.exchange.template.body}")
	private String oidcCodeExchangeBodyTemplate;

	/** Access Token URL. */
	@Value("${avenirs.authentication.oidc.token.url}")
	private String oidcAccessTokenURL;


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

	@Value("${avenirs.authentication.oidc.token.is.jwt}")
	private boolean jwtAccessToken;

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
		if (log.isTraceEnabled()) {
			String maskedCode = code == null ? "null" : "*".repeat(code.length());
			String maskedOIDCAuthorizeURL = String.format(oidcAuthorizeTemplate, host, host, maskedCode);
			log.trace("generateAuthorizeURL, host: {}", host);
			log.trace("generateAuthorizeURL, code: {}", maskedCode);

			log.trace("generateAuthorizeURL, oidcAuthorizeURL: {}", maskedOIDCAuthorizeURL);
		}
		return oidcAuthorizeURL;
	}

	/**
	 * Generates the OIDC Access Token Body query.
	 * 
	 * @param login    The user login.
	 * @param password The user password.
	 * @return The access token body.
	  */
	protected String generateAccessTokenBody(String login, String password) {
		String oidcAccessTokenBody = String.format(oidcAccessTokenBodyTemplate, login, password);

		if (log.isDebugEnabled()) {
			String maskedPassword = password == null ? "null": "*".repeat(password.length());
			String maskedOIDCAccessTokenBody = String.format(oidcAccessTokenBody, login, maskedPassword);
			log.debug("generateAccessTokenBody, maskedOIDCAccessTokenBody: {}", maskedOIDCAccessTokenBody);
		}
		return oidcAccessTokenBody;

	}
	/**
	 * Generates the OIDC code exchange Body query.
	 *
	 * @param host    The host associated to the service (for redirect_uri).
	 * @param code The code given by the oidc provider.
	 * @return The exchange query body.
	  */
	protected String generateCodeExchangeBody(String host, String code) {
		String oidcCodeExchangeBody = String.format(oidcCodeExchangeBodyTemplate, host, code);

		if (log.isDebugEnabled()) {
			String maskedCode = code == null ? "null": "*".repeat(code.length());
			String maskedOIDCCodeExchangeBody = String.format(oidcCodeExchangeBodyTemplate, host, maskedCode);
			log.debug("generateCodeExchangeBody, maskedOIDCCodeExchangeBody: {}", maskedOIDCCodeExchangeBody);
		}
		return oidcCodeExchangeBody;

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
		if (log.isTraceEnabled()) {
			String maskedIntrospectURL = String.format(oidcProviderIntrospectURL, token.substring(0, 4) + "****" + token.substring(token.length() - 4));
			log.trace("generateIntrospectURL, maskedIntrospectURL: {}", maskedIntrospectURL);
		}

		return introspectURL;
	}

	public OIDCAccessTokenResponse exchangeAuthorizationCodeForToken(String host, String code) {

		String body = generateCodeExchangeBody(host, code);
		return restClient.post()
				.uri(oidcAccessTokenURL)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.body(body)
				.retrieve()
				.body(OIDCAccessTokenResponse.class);
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

			String body = generateAccessTokenBody(login, password);

			OIDCAccessTokenResponse response = restClient.post()
					.uri(oidcAccessTokenURL)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.retrieve()
					.body(OIDCAccessTokenResponse.class);



			if (response == null) {
				return Optional.empty();
			}

			log.debug("getAccessToken, jwtAccessToken: {}", jwtAccessToken);

			if (jwtAccessToken) {
				response.setJwt(true);


				final Optional<Claims> claims = this.jwtService.parseAndCheckSignature(response);
				if (claims.isEmpty()) {
					log.error("Invalid access token response: {}", response);
					return Optional.empty();
				}
				log.trace("Claims: {}", claims.get());
				response.setClaims(new HashMap<>(claims.get()));
			}
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
