package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final long ttlSeconds;
  private final String algorithm;
  private final String secret;

  public HmacAuthContextSigningService(
      ObjectMapper objectMapper,
      @Value("${avenirs.security.context.signature.ttl-seconds:300}") long ttlSeconds,
      @Value("${avenirs.security.context.signature.algorithm:HmacSHA256}") String algorithm,
      @Value("${security.hmac.secret}") String secret) {
    this.objectMapper = objectMapper;
    this.ttlSeconds = ttlSeconds;
    this.algorithm = algorithm;
    this.secret = secret;
  }

  @Override
  public SignedAuthContext sign(AuthContext authContext) {
    try {
      long now = Instant.now().getEpochSecond();

      // TODO: Mock while waiting #1683
      SignedContextPayload payload =
          new SignedContextPayload(authContext.login(), now, now + ttlSeconds);

      String jsonPayload = objectMapper.writeValueAsString(payload);

      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));

      String signature =
          Base64.getEncoder()
              .encodeToString(mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));

      return new SignedAuthContext(jsonPayload, signature);

    } catch (Exception e) {
      throw new IllegalStateException("Unable to sign authentication context", e);
    }
  }

  private record SignedContextPayload(String sub, long iat, long exp) {}
}
