package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.enums.ESecurityKeys;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthContextSigningPort;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HmacAuthContextSigningService implements AuthContextSigningPort {

  private final ObjectMapper objectMapper;
  private final String currentKid;
  private final long ttlSeconds;
  private final String algorithm;

  public HmacAuthContextSigningService(
      ObjectMapper objectMapper,
      @Value("${avenirs.security.context.signature.current-kid:v2}") String currentKid,
      @Value("${avenirs.security.context.signature.ttl-seconds:300}") long ttlSeconds,
      @Value("${avenirs.security.context.signature.algorithm:HmacSHA256}") String algorithm) {
    this.objectMapper = objectMapper;
    this.currentKid = currentKid;
    this.ttlSeconds = ttlSeconds;
    this.algorithm = algorithm;
  }

  @Override
  public SignedAuthContext sign(AuthContext authContext) {
    try {
      long now = Instant.now().getEpochSecond();

      SignedContextPayload payload =
          new SignedContextPayload(authContext.userId().toString(), now, now + ttlSeconds);

      String jsonPayload = objectMapper.writeValueAsString(payload);
      String secret = ESecurityKeys.getSecretByKey(currentKid);

      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));

      String signature =
          Base64.getEncoder()
              .encodeToString(mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));

      return new SignedAuthContext(jsonPayload, signature, currentKid);

    } catch (Exception e) {
      throw new IllegalStateException("Unable to sign authentication context", e);
    }
  }

  private record SignedContextPayload(String sub, long iat, long exp) {}
}
