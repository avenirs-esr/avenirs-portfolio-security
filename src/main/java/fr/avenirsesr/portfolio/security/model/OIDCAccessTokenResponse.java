package fr.avenirsesr.portfolio.security.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * OIDC Response of a query to fetch an access token.
 */
@Data()
@Accessors(chain=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OIDCAccessTokenResponse {
	
	/** Access token value */
	@ToString.Exclude
	@JsonProperty("access_token")
	private String accessToken;
	
	/** Id token value */
	@JsonProperty("id_token")
	private OIDCIdToken idToken;
		
	/** Access token type (e.g. "Bearer") */
	@JsonProperty("token_type")
	private String tokenType;
	
	/** Remaining time in seconds. */
	@JsonProperty("expires_in")
	private int expireIn;
	
	/** Scope requested (e.g.: "openid")*/
	private String scope;
	
	/** Claims of the access token. */
	private  Map<String, Object> claims;

	/** Flag to determine if the access token is a JWT. */
	private boolean jwt;

	@ToString.Include(name = "accessToken")
	public String getMaskedAccessToken() {
		return accessToken != null ? "****"  : null;
	}
	

}
