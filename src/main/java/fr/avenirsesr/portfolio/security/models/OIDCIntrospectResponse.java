package fr.avenirsesr.portfolio.security.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * OIDC introspection response from the OIDC provider for a given access token.
 * Most part of the field in the OIDC provider response are filtered.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OIDCIntrospectResponse {
	
	/** The access token. */
	private String token;
	
	/** Flag to determine if the account exists and is active. */
	private boolean active;
	
	/** Id of the user. */
	private String uniqueSecurityName;
}
