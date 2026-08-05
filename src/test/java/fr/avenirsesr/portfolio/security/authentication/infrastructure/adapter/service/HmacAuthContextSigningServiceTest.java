package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HmacAuthContextSigningServiceTest {

  private static final long TTL_SECONDS = 300L;
  private static final String ALGORITHM = "HmacSHA256";
  private static final String SECRET = "test-secret";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Nested
  class GivenHmacAuthContextSigningService {

    private HmacAuthContextSigningService service;

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an HMAC auth context signing service");

      service = new HmacAuthContextSigningService(objectMapper, TTL_SECONDS, ALGORITHM, SECRET);
    }

    @Nested
    class WhenSigningAuthContext {
      private AuthContext authContext;
      private SignedAuthContext result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("signing an auth context");

        authContext = new AuthContext(true, "gribonvald", Set.of("rbac:read", "profile:read:own"));
        result = service.sign(authContext);
      }

      @Test
      void thenItShouldReturnSignedAuthContext() {
        BddLogger.then("it should return a signed auth context");

        assertNotNull(result);
        assertNotNull(result.payload());
        assertNotNull(result.signature());
        assertFalse(result.payload().isBlank());
        assertFalse(result.signature().isBlank());
      }

      @Test
      void thenItShouldCreatePayloadWithSubIatExpAndAuthorities() throws Exception {
        BddLogger.then("it should create payload with sub, iat, exp and authorities");

        SignedPayload payload = objectMapper.readValue(result.payload(), SignedPayload.class);

        assertEquals("gribonvald", payload.sub());
        assertTrue(payload.iat() > 0);
        assertEquals(TTL_SECONDS, payload.exp() - payload.iat());
        assertEquals(Set.of("rbac:read", "profile:read:own"), payload.authorities());
      }

      @Test
      void thenItShouldGenerateValidHmacSignature() throws Exception {
        BddLogger.then("it should generate a valid HMAC signature");

        String expectedSignature = sign(result.payload(), SECRET);

        assertEquals(expectedSignature, result.signature());
      }
    }
  }

  private String sign(String payload, String secret) throws Exception {
    Mac mac = Mac.getInstance(ALGORITHM);
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));

    return Base64.getEncoder()
        .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }

  private record SignedPayload(String sub, long iat, long exp, Set<String> authorities) {}
}
