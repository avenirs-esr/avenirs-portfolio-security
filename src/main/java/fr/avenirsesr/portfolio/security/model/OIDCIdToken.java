package fr.avenirsesr.portfolio.security.model;

import java.util.Base64;

import lombok.ToString;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data()
@Accessors(chain=true)
public class OIDCIdToken {
	private String header;
	@ToString.Exclude
	private String payload;
	@ToString.Exclude
	private String signature;

	@ToString.Exclude
	private String rawIdToken;
	
	public OIDCIdToken(String rawIdToken) {
		this.rawIdToken = rawIdToken;
		if (StringUtils.hasLength(rawIdToken)) {
			String[]tokens = rawIdToken.split("\\.");
			this.header = decodeToken(tokens[0]);
			this.payload = tokens.length > 1 ? decodeToken(tokens[1]) : "";
			this.signature = tokens.length == 3 ?tokens[2] : "";
		}	
	}
	
	private String decodeToken(String token) {
		byte[] decodedBytes = Base64.getDecoder().decode(token);
		return  new String(decodedBytes);
	}


	@ToString.Include(name = "signature")
	public String getMaskedSignature() {
		return signature != null ? "****"  : null;
	}

	@ToString.Include(name = "payload")
	public String getMaskedPayload() {
		return payload != null ? "****"  : null;
	}

}
