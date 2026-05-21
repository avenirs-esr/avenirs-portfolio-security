package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.enums.ESecurityKeys;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HmacAuthContextSigningServiceTest {

  private static final String KID = "v2";
  private static final long TTL_SECONDS = 300L;
  private static final String ALGORITHM = "HmacSHA256";
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Nested
  class GivenHmacAuthContextSigningService {

    private HmacAuthContextSigningService service;

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an HMAC auth context signing service");

      service = new HmacAuthContextSigningService(objectMapper, KID, TTL_SECONDS, ALGORITHM);
    }

    @Nested
    class WhenSigningAuthContext {
      private AuthContext authContext;
      private SignedAuthContext result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("signing an auth context");

        authContext = new AuthContext(true, USER_ID, "gribonvald");
        result = service.sign(authContext);
      }

      @Test
      void thenItShouldReturnSignedAuthContext() {
        BddLogger.then("it should return a signed auth context");

        assertNotNull(result);
        assertEquals(KID, result.kid());
        assertNotNull(result.payload());
        assertNotNull(result.signature());
        assertFalse(result.payload().isBlank());
        assertFalse(result.signature().isBlank());
      }

      @Test
      void thenItShouldCreatePayloadWithSubIatAndExp() throws Exception {
        BddLogger.then("it should create payload with sub, iat and exp");

        SignedPayload payload = objectMapper.readValue(result.payload(), SignedPayload.class);

        assertEquals(USER_ID.toString(), payload.sub());
        assertTrue(payload.iat() > 0);
        assertEquals(TTL_SECONDS, payload.exp() - payload.iat());
      }

      @Test
      void thenItShouldGenerateValidHmacSignature() throws Exception {
        BddLogger.then("it should generate a valid HMAC signature");

        String expectedSignature = sign(result.payload(), ESecurityKeys.getSecretByKey(KID));

        assertEquals(expectedSignature, result.signature());
      }
    }

    @Nested
    class WhenSigningAuthContextWithInvalidKid {
      private HmacAuthContextSigningService serviceWithInvalidKid;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("signing an auth context with invalid kid");

        serviceWithInvalidKid =
            new HmacAuthContextSigningService(objectMapper, "unknown", TTL_SECONDS, ALGORITHM);
      }

      @Test
      void thenItShouldThrowIllegalStateException() {
        BddLogger.then("it should throw illegal state exception");

        assertThrows(
            IllegalStateException.class,
            () -> serviceWithInvalidKid.sign(new AuthContext(true, USER_ID, "gribonvald")));
      }
    }
  }

  private String sign(String payload, String secret) throws Exception {
    Mac mac = Mac.getInstance(ALGORITHM);
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));

    return Base64.getEncoder()
        .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }

  private record SignedPayload(String sub, long iat, long exp) {}
}
