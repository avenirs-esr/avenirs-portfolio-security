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
	private String id;
	private String service;
	private boolean active;
	private String givenName;
	private String familyName;
	private String email;
	
	@JsonProperty("attributes")
	private void flattenAttributes(Map<String, Object> attributes) {
		this.givenName = (String)attributes.get("given_name");
		this.familyName = (String)attributes.get("family_name");
		this.email = (String)attributes.get("email");
	}
}
