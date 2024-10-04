package fr.avenirsesr.portfolio.security.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

/**
 * OIDC introspection response from the OIDC provider for a given access token.
 * Most part of the field in the OIDC provider response are filtered.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OIDCIntrospectResponse {
	
	/** The access token. */
	@ToString.Exclude
	private String token;
	
	/** Flag to determine if the account exists and is active. */
	private boolean active;
	
	/** ID of the user. */
	private String uniqueSecurityName;

	@ToString.Include(name = "token")
	public String getMaskedToken() {
		return token != null ? "****"  : null;
	}
}
