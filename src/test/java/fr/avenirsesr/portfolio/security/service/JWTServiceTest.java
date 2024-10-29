package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.configuration.JWTToCryptographicKeyAlgoMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import fr.avenirsesr.portfolio.security.model.OIDCIdToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


class JWTServiceTest {


    @Mock
    private JWTToCryptographicKeyAlgoMapper algoMapper;

    @InjectMocks
    private JWTService jwtService;


    private AutoCloseable closeable;
    private MockWebServer mockWebServer;

    private static final String TEST_KID = "testKid";
    private static final String TEST_ALG = "RS256";


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
        closeable = MockitoAnnotations.openMocks(this);
        Field urlField = JWTService.class.getDeclaredField("oidcJWKSURL");
        urlField.setAccessible(true);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        urlField.set(jwtService, mockWebServer.url("/jwks").toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
        mockWebServer.shutdown();
    }

    @Test

    void testGetPublicKeySuccess() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"" + TEST_KID + "\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
        when(algoMapper.mapJWTToCryptographicKey(TEST_ALG)).thenReturn(Optional.of("RSA"));

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isPresent());
        assertInstanceOf(RSAPublicKey.class, result.get() );
    }
    @Test
    void testGetPublicKeyFailureKeyNotFound() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"unknownKid\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
        when(algoMapper.mapJWTToCryptographicKey(TEST_ALG)).thenReturn(Optional.of("RSA"));

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPublicKeyFailureInvalidAlgorithm() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"" + TEST_KID + "\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"invalidAlg\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
        when(algoMapper.mapJWTToCryptographicKey("invalidAlg")).thenReturn(Optional.empty());

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPublicKeyFailureEmptyKID() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"\", \"alg\": \"invalidAlg\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPublicKeyFailureEmptyAlgorithm() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"" + TEST_KID + "\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
       // when(algoMapper.mapJWTToCryptographicKey("invalidAlg")).thenReturn(Optional.empty());

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPublicKeyFailureInvalidJWKSFormat() {
        String invalidJwksResponse = "invalidJWKS";
        mockWebServer.enqueue(new MockResponse().setBody(invalidJwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" }";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
        when(algoMapper.mapJWTToCryptographicKey(TEST_ALG)).thenReturn(Optional.of("RSA"));

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPublicKeyFailureInvalidIdTokenFormat() {
        //noinspection SpellCheckingInspection
        String jwksResponse = "{ \"keys\": [{ \"kid\": \"" + TEST_KID + "\", \"n\": \"AKOx0nEfb2MP3iBwpQZ5Tx7gA5Mje6UAVAnzjEY64IBDdK5B9UPu7vE2YffIDVwo5KeJlWyNqkOBh5n8OEpuE6A=\", \"e\": \"AQAB\" }] }";
        mockWebServer.enqueue(new MockResponse().setBody(jwksResponse).setResponseCode(200));

        String headerJson = "{ \"kid\": \"" + TEST_KID + "\", \"alg\": \"" + TEST_ALG + "\" ";
        OIDCIdToken idToken = mock(OIDCIdToken.class);
        when(idToken.getHeader()).thenReturn(headerJson);
        when(algoMapper.mapJWTToCryptographicKey(TEST_ALG)).thenReturn(Optional.of("RSA"));

        Optional<PublicKey> result = jwtService.getPublicKey(idToken);

        assertTrue(result.isEmpty());
    }
}