package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration.JWTToCryptographicKeyAlgoMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCIdToken;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

  private static byte[] unsignedBytes(BigInteger bigInteger) {
    byte[] bytes = bigInteger.toByteArray();
    return (bytes.length > 1 && bytes[0] == 0) ? Arrays.copyOfRange(bytes, 1, bytes.length) : bytes;
  }

  @Mock private JWTToCryptographicKeyAlgoMapper algoMapper;

  @InjectMocks private JWTService jwtService;
  private MockWebServer mockWebServer;

  private static final String TEST_KID = "testKid";
  private static final String TEST_ALG = "RS256";

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
    Field urlField = JWTService.class.getDeclaredField("oidcJWKSURL");
    urlField.setAccessible(true);
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    urlField.set(jwtService, mockWebServer.url("/jwks").toString());
  }

  @AfterEach
  void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @Test
  void testGetPublicKeySuccess() {
    BddLogger.given("a JWKS endpoint returning a public key matching the token kid");
    KeyPairGenerator keyPairGenerator;
    try {
      keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    } catch (Exception e) {
      fail(e);
      return;
    }
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    String n = Base64.getUrlEncoder().encodeToString(unsignedBytes(publicKey.getModulus()));
    String e = Base64.getUrlEncoder().encodeToString(unsignedBytes(publicKey.getPublicExponent()));

    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \""
            + TEST_KID
            + "\", \"n\": \""
            + n
            + "\", \"e\": \""
            + e
            + "\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header containing kid and alg");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
    when(algoMapper.mapJWTToCryptographicKey(TEST_ALG)).thenReturn(Optional.of("RSA"));

    OIDCIdToken idToken = new OIDCIdToken(null);
    idToken.setHeader(headerJson);

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return a RSA public key");
    assertTrue(result.isPresent());
    assertInstanceOf(RSAPublicKey.class, result.get());
  }

  @Test
  void testGetPublicKeyFailureKeyNotFound() {
    BddLogger.given("a JWKS endpoint returning a key with a different kid");
    //noinspection SpellCheckingInspection
    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \"unknownKid\", \"n\":"
            + " \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\","
            + " \"e\": \"AQAB\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header targeting a kid that is not present in JWKS");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublicKeyFailureInvalidAlgorithm() {
    BddLogger.given("a JWKS endpoint returning a public key matching the token kid");
    //noinspection SpellCheckingInspection
    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \""
            + TEST_KID
            + "\", \"n\":"
            + " \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\","
            + " \"e\": \"AQAB\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header with an algorithm not supported by the mapper");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"invalidAlg\" }";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);
    when(algoMapper.mapJWTToCryptographicKey("invalidAlg")).thenReturn(Optional.empty());

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublicKeyFailureEmptyKID() {
    BddLogger.given("a JWKS endpoint returning a key with an empty kid");
    //noinspection SpellCheckingInspection
    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \"\", \"n\":"
            + " \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\","
            + " \"e\": \"AQAB\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header with an empty kid");
    String headerJson = "{ \"kid\": \"\", \"alg\": \"invalidAlg\" }";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublicKeyFailureEmptyAlgorithm() {
    BddLogger.given("a JWKS endpoint returning a public key matching the token kid");
    //noinspection SpellCheckingInspection
    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \""
            + TEST_KID
            + "\", \"n\":"
            + " \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\","
            + " \"e\": \"AQAB\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header with an empty algorithm");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"\" }";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);
    // when(algoMapper.mapJWTToCryptographicKey("invalidAlg")).thenReturn(Optional.empty());

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublicKeyFailureInvalidJWKSFormat() {
    BddLogger.given("a JWKS endpoint returning an invalid JWKS payload");
    String invalidJwksResponse = "invalidJWKS";
    mockWebServer.enqueue(new MockResponse().setBody(invalidJwksResponse).setResponseCode(200));

    BddLogger.and("an id token header containing kid and alg");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublicKeyFailureInvalidIdTokenFormat() {
    BddLogger.given("a JWKS endpoint returning a public key matching the token kid");
    //noinspection SpellCheckingInspection
    String jwksResponse =
        "{ \"keys\": [{ \"kid\": \""
            + TEST_KID
            + "\", \"n\":"
            + " \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\","
            + " \"e\": \"AQAB\" }] }";
    mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

    BddLogger.and("an id token header that is not valid JSON");
    String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" ";
    OIDCIdToken idToken = mock(OIDCIdToken.class);
    when(idToken.getHeader()).thenReturn(headerJson);

    BddLogger.when("requesting the public key from the JWT service");
    Optional<PublicKey> result = jwtService.getPublicKey(idToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testParseAndCheckSignatureFailureNullAccessTokenResponse() {
    BddLogger.given("a null access token response");

    BddLogger.when("parsing and checking signature");
    Optional<Map<String, Object>> result = jwtService.parseAndCheckSignature(null);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void testParseAndCheckSignatureFailureEmptyKey() {
    BddLogger.given("an access token response with an empty idToken");

    OIDCAccessToken accessToken =
        new OIDCAccessToken(
            "TEST_ACCESS_TOKEN", "TEST_REFRESH_TOKEN", null, 0, null, "", null, false);

    BddLogger.when("parsing and checking signature");
    Optional<Map<String, Object>> result = jwtService.parseAndCheckSignature(accessToken);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }
}
