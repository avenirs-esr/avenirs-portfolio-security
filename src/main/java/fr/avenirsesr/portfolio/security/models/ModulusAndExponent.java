package fr.avenirsesr.portfolio.security.models;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Modulus and Exponent of a RSA key, for instance fetched from JWKS (JSON Web Key Sets).
 * Can be used to generate a public Key to check a signed JWT.
 */
@Data
@Accessors(chain=true)
@AllArgsConstructor
public class ModulusAndExponent {
	
	/** Modulus part.*/
	private BigInteger modulus;
	
	/** Exponent part. */
	private BigInteger exponent;
	
	
}
