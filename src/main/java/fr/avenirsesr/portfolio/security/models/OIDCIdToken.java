package fr.avenirsesr.portfolio.security.models;

import java.util.Base64;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain=true)
public class OIDCIdToken {
	private String rawIdToken;
	private String header;
	private String payload;
	private String signature;
	
	public OIDCIdToken(String rawIdToken) {
		this.rawIdToken = rawIdToken;
		if (StringUtils.hasLength(rawIdToken)) {
			String[]tokens = rawIdToken.split("\\.");
			this.header = decodeToken(tokens[0]);
			this.payload = tokens.length > 0 ? decodeToken(tokens[1]) : "";
			this.signature = tokens.length == 3 ?tokens[2] : "";
//			Jwts.parser()
//			.build().
		}	
	}
	
	private String decodeToken(String token) {
		byte[] decodedBytes = Base64.getDecoder().decode(token);
		return  new String(decodedBytes);
	}

}
