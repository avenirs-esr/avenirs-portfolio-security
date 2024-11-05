package fr.avenirsesr.portfolio.security.service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import fr.avenirsesr.portfolio.security.configuration.JWTToCryptographicKeyAlgoMapper;
import fr.avenirsesr.portfolio.security.model.ModulusAndExponent;
import fr.avenirsesr.portfolio.security.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.model.OIDCIdToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

/**
 * Service used to checks and parse JWT. 
 * @implNote Only RSA algorithms are supported to generate the public keys.
 */

@Slf4j
@Service()
public class JWTService {

	/** Mapping between jwt and cryptographic key algorithm (e.g. RS256 -> RSA). */
	@Autowired
	private JWTToCryptographicKeyAlgoMapper algoMapper;

	/** OIDC provider JWKS end point. */
	@Value("${avenirs.authentication.oidc.provider.jwks.url}")
	private String oidcJWKSURL;
	
	/** Public keys indexed by their corresponding kid in the JWKS. */
	private final Map<String, PublicKey> keysRepository = new HashMap<>();

	/** Rest client to retrieve JWKS data. */
	private final RestClient client = RestClient.create();
	
	

	/**
	 * Retrieves the modulus and exponent for an RSA key.
	 * @param kid The key id.
	 * @return The modulus and exponent fetched from the OIDC provider end point.
	 */
	private Optional<ModulusAndExponent> fetchModulusAndExponent(String kid) {
		try {
			// Fetches the JWKS from the OIDC provider end point.
			String jwksResponse = client.get().uri(oidcJWKSURL).retrieve().body(String.class);

			// Extracts the modulus and exponent corresponding to the key id (kid).
			return extractModulusAndExponent(jwksResponse, kid);

		} catch (RestClientResponseException e) {
			log.error(
					"fetchModulusAndExponent, unable to fetch Modulus and Exponent. HTTP Status code: {}, response body: {}",
					e.getStatusCode().value(), e.getResponseBodyAsString());
			log.error("Exception: ", e);

		} catch (Exception e) {
			log.error("Exception: ", e);
		}
		return Optional.empty();
	}

	/**
	 * Extracts the modulus and exponent corresponding to a key id from a JWKS response.
	 * @param jwksResponse The JWKS .
	 * @param kid The key id of the key associated to the modulus &amp; exponent.
	 * @return An Optional of ModulusAndExponent.
	 */
	protected Optional<ModulusAndExponent> extractModulusAndExponent(String jwksResponse, String kid) {
		try {
			JSONObject jsonObject = new JSONObject(jwksResponse);
			JSONArray keys = jsonObject.getJSONArray("keys");

			for (int i = 0; i < keys.length(); i++) {
				JSONObject key = keys.getJSONObject(i);

				String kidToTest = key.getString("kid");
				if (kidToTest.equals(kid)) {
					String modulusBase64Url = key.getString("n");
					String exponentBase64Url = key.getString("e");

					String modulusBase64 = modulusBase64Url.replace("-", "+").replace("_", "/");
					String exponentBase64 = exponentBase64Url.replace("-", "+").replace("_", "/");

					byte[] modulusBytes = Base64.getDecoder().decode(modulusBase64);
					byte[] exponentBytes = Base64.getDecoder().decode(exponentBase64);

					BigInteger modulus = new BigInteger(1, modulusBytes);
					BigInteger exponent = new BigInteger(1, exponentBytes);

					return Optional.of(new ModulusAndExponent(modulus, exponent));
				}
			}
		} catch (Exception e) {
			log.error("extractModulusAndExponent, error", e);
		}
		return Optional.empty();
	}


	/**
	 * Generates an RSA public key.
	 * @param alg The algorithm specified in the id token.
	 * @param modulusAndExponent The modulus and exponent.
	 * @return An Optional of the Public key.
	 */
	protected Optional<PublicKey> generateRSAPublicKey(String alg, ModulusAndExponent modulusAndExponent) {

		try {
			// Builds the key specification.
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulusAndExponent.getModulus(),
				modulusAndExponent.getExponent());

		
			final Optional<String> keyAlgo = algoMapper.mapJWTToCryptographicKey(alg);
			if (keyAlgo.isEmpty()) {
				log.error("generatePublicKey, unable to map jwt alg {}, from mapping defined in properties", alg);
				return Optional.empty();
			}
			
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgo.get());
			return Optional.of(keyFactory.generatePublic(rsaPublicKeySpec));
		} catch (NoSuchAlgorithmException e) {
			log.error("generateRSAPublicKey, invalid algorithm alg: {}", alg);
			log.error("generateRSAPublicKey", e);
		} catch (InvalidKeySpecException e) {
			log.error("generateRSAPublicKey", e);
		}
		return Optional.empty();
	}

	/**
	 * Gives the public key associated to an idToken header. If the key does not
	 * exist in the key repository it is generated.
	 * 
	 * @param idToken The id token which contains in its header the properties kid and alg.
	 * @return An optional of the publicKey.
	 */
	protected Optional<PublicKey> getPublicKey(OIDCIdToken idToken) {
		if (idToken != null && StringUtils.hasLength(idToken.getHeader())) {

			try {
				JSONObject jsonObject = new JSONObject(idToken.getHeader());
				String kid = jsonObject.getString("kid");

				if (!StringUtils.hasLength(kid)) {
					log.error("getPublicKey, Unable to fetch kid from idToken: {}", idToken);
					return Optional.empty();
				}

				// The key is not in the repository so it is generated.
				if (!this.keysRepository.containsKey(kid)) {
					String alg = jsonObject.getString("alg");

					if (!StringUtils.hasLength(alg)) {
						log.error("getPublicKey, Unable to fetch alg from idToken: {}", idToken);
						return Optional.empty();
					}
					
					
					Optional<ModulusAndExponent> modulusAndExponent = this.fetchModulusAndExponent(kid);
					if (modulusAndExponent.isEmpty()) {
						return Optional.empty();
					}

					Optional<PublicKey> publicKey = this.generateRSAPublicKey(alg, modulusAndExponent.get());

					if (publicKey.isEmpty()) {
						log.error("getPublicKey, Error while trying to generate public key from kid{}, idToken: {}",
								kid, idToken);
						return Optional.empty();
					}

					this.keysRepository.put(kid, publicKey.get());
				}
				return Optional.of(this.keysRepository.get(kid));

			} catch (JSONException e) {
				log.error("getPublicKey invalid JSON", e);
			} catch (Exception e) {
				log.error("getPublicKey", e);
			}
		}
		return Optional.empty();

	}

	/**
	 * Parses an access token and checks its signature.
	 * @param accessTokenResponse The access token response which contains the
	 *                            access token and the id token.
	 * @return An Optional of Claims.
	 */
	public Optional<Claims> parseAndCheckSignature(OIDCAccessTokenResponse accessTokenResponse) {
		if (accessTokenResponse == null) {
			return Optional.empty();
		}
		final Optional<PublicKey> publicKey = getPublicKey(accessTokenResponse.getIdToken());
		if (publicKey.isEmpty()) {
			log.error("Empty public key while trying to fetch claims from accessTokenResponse: {}",
					accessTokenResponse);
			return Optional.empty();
		}

		try {
			// Parses and checks signature.
			final Claims claims = Jwts.parser()
					.verifyWith(publicKey.get())
					.build()
					.parseSignedClaims(accessTokenResponse.getAccessToken()).getPayload();

			return Optional.of(claims);
		} catch (SignatureException e) {
			log.error("Invalid signature of access token in access token response: {}", accessTokenResponse);
			return Optional.empty();
		}
	}
}
