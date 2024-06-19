/**
 * 
 */
package fr.avenirsesr.portfolio.security.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * OIDCSettings
 * @author Arnaud Deman
 * 2024-03-14
 */
@Data
@Component
@PropertySource(value = "classpath:oidc-settings.json")
@ConfigurationProperties
public class OIDCConfiguration {
	
	/** OIDC Client ID. */
	private String clientId;
	
	/** OIDC Client secret. */
	private String clientSecret;
	
	

	/**
	 * 
	 */
	public OIDCConfiguration() {
		// TODO Auto-generated constructor stub
	}

}
