package fr.avenirsesr.portfolio.security.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * Mapping from algorithm property in an id token header to cryptographic key algorithm.
 */
@Component
@ConfigurationProperties(prefix = "avenirs.jwt.alg")
@Data
public class JWTToCryptographicKeyAlgoMapper{

	/** Mapping JWT to Cryptographic key algorithm. */
    private Map<String, String> mapping;
    
    
    /**
     * Map a JWT algorithm to the corresponding algorithm to generate a cryptographic key.
     * @param jwtAlgo The JWT algorithm.
     * @return An Optional with the PEM key algorithm.
     */
    public Optional<String> mapJWTToCryptographicKey(String jwtAlgo) {
    	
    	if (jwtAlgo == null) {
    		return Optional.empty();
    	}
    	final String key = jwtAlgo.toUpperCase();
    	
    	return mapping.containsKey(key)? Optional.of(mapping.get(jwtAlgo.toUpperCase())): Optional.empty();
    }

}
