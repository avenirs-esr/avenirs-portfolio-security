package fr.avenirsesr.portfolio.security.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * OIDC Response of a query to fetch an access token.
 */
@Data()
@Accessors(chain=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OIDCAccessTokenResponse {
	
	/** Access token value */
	@JsonProperty("access_token")
	private String accessToken;
	
	/** Id token value */
	@JsonProperty("id_token")
	private String idToken;
		
	/** Access token type (e.g. "Bearer") */
	@JsonProperty("token_type")
	private String tokenType;
	
	/** Remaining time in seconds. */
	@JsonProperty("expires_in")
	private int expireIn;
	
	/** Scope requested (e.g.: "openid")*/
	private String scope;
	
	/** Error if the query is not successful. */
	private Exception error;
	

}
