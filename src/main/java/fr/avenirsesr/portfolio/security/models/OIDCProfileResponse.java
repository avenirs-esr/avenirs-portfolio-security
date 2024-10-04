package fr.avenirsesr.portfolio.security.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * OIDC profile response from the OIDC provider for a given access token.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OIDCProfileResponse {
	
	/** ID of the user. */
	private String id;
	
	/** Service associated to the access token. */
	private String service;

	/** First name. */
	private String firstName;
	
	/** Last name. */
	private String lastName;

	/** Email of the principal. */
	private String email;
	
	/**
	 * Hydrates the instance.
	 * @param attributes The attributes for the hydration. 
	 */
	@JsonProperty("attributes")
	private void flattenAttributes(Map<String, Object> attributes) {
		this.firstName = (String)attributes.get("given_name");
		this.lastName = (String)attributes.get("family_name");
		this.email = (String)attributes.get("email");
	}
}
