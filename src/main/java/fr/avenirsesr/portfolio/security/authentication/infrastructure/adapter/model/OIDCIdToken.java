package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Base64;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

@Data
@Accessors(chain = true)
public class OIDCIdToken {
  private String header;
  @ToString.Exclude private String payload;
  @ToString.Exclude private String signature;

  @ToString.Exclude private String rawIdToken;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public OIDCIdToken(String rawIdToken) {
    this.rawIdToken = rawIdToken;
    if (StringUtils.hasLength(rawIdToken)) {
      String[] tokens = rawIdToken.split("\\.");
      this.header = decodeToken(tokens[0]);
      this.payload = tokens.length > 1 ? decodeToken(tokens[1]) : "";
      this.signature = tokens.length == 3 ? tokens[2] : "";
    }
  }

  private String decodeToken(String token) {
    String paddedToken = token;
    int pad = token.length() % 4;
    if (pad != 0) {
      paddedToken = token + "=".repeat(4 - pad);
    }
    byte[] decodedBytes = Base64.getUrlDecoder().decode(paddedToken);
    return new String(decodedBytes);
  }

  @ToString.Include(name = "signature")
  public String getMaskedSignature() {
    return signature != null ? "****" : null;
  }

  @ToString.Include(name = "payload")
  public String getMaskedPayload() {
    return payload != null ? "****" : null;
  }
}
