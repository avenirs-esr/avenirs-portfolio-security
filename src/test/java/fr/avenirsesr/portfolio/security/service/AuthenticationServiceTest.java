package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.model.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.model.OIDCProfileResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

/**
 * <h1>AuthenticationServiceTest</h1>
 * <p>
 * <b>Description:</b> Test case for AuthenticationService.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 03/10/2024
 */

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationServiceTest {
    @Value("${avenirs.test.authentication.service.authorise.expected.url}")
    private String authoriseExpectedURL;

    @Value("${avenirs.test.authentication.service.host}")
    private String host;

    @Value("${avenirs.test.authentication.service.code}")
    private String code;

    @Value("${avenirs.test.authentication.service.profile.expected.url}")
    private String profileExpectedURL;

    @Value("${avenirs.test.authentication.service.introspect.expected.url}")
    private String introspectExpectedURL;

    @Value("${avenirs.test.authentication.service.service.expected.url}")
    private String serviceExpectedURL;

    @Value("${avenirs.test.authentication.service.user.login}")
    private String userLogin;

    @Value("${avenirs.test.authentication.service.user.password}")
    private String userPassword;

    @Value("${avenirs.test.authentication.service.user.email}")
    private String userEmail;

    @Value("${avenirs.test.authentication.service.user.fist.name}")
    private String userFirstName;

    @Value("${avenirs.test.authentication.service.user.last.name}")
    private String userLastName;

    @Value("${avenirs.test.authentication.service.token}")
    private String token;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AccessTokenHelper accessTokenHelper;

    @Test
    void generateAuthorizeURL() {
        assertEquals(authoriseExpectedURL, authenticationService.generateAuthorizeURL(host, code), "Generated authorize URL.");
    }

    @Test
    void generateServiceURL() {
        assertEquals(serviceExpectedURL, authenticationService.generateServiceURL(host), "Generated service URL.");
    }

    @Test
    void generateProfileURL() {
        assertEquals(profileExpectedURL, authenticationService.generateProfileURL(token), "Generated profile URL.");
    }

    @Test
    void generateIntrospectURL() {
        assertEquals(introspectExpectedURL, authenticationService.generateIntrospectURL(token), "Generated introspect URL.");
    }

    @Test
    void profile() throws Exception {

        OIDCProfileResponse response = authenticationService.profile(accessTokenHelper.provideAccessToken(userLogin, userPassword));
        assertThat(response)
                .extracting(OIDCProfileResponse::getId,
                        OIDCProfileResponse::getFirstName,
                        OIDCProfileResponse::getLastName,
                        OIDCProfileResponse::getEmail)
                        .containsExactly(userLogin, userFirstName, userLastName, userEmail);
    }

    @Test
    void introspectAccessToken() throws Exception {
        final String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
        OIDCIntrospectResponse response = authenticationService.introspectAccessToken(token);
        assertEquals(token, response.getToken(), "Access token in response");
        assertTrue(response.isActive(), "Active flag in response");
        assertEquals(userLogin, response.getUniqueSecurityName(),"Unique security name in response");


    }

    @Test
    void getAccessTokenWithValidPassword() {

        Optional<OIDCAccessTokenResponse> response = authenticationService.getAccessToken(userLogin, userPassword);
        assertFalse(response.isEmpty());
        OIDCAccessTokenResponse oidcAccessTokenResponse = response.get();
        if (oidcAccessTokenResponse.isJwt()) {
            assertNotNull(oidcAccessTokenResponse.getIdToken(), "Id Token in response");
            assertNotNull(oidcAccessTokenResponse.getClaims(), "Claims in response");
            assertFalse(oidcAccessTokenResponse.getClaims().isEmpty(), "Claims not empty");
        }
        assertNotNull(oidcAccessTokenResponse.getAccessToken(), "Access Token in response");
        assertFalse(oidcAccessTokenResponse.getAccessToken().isEmpty(), "Access Token not empty");
    }

    @Test
    void getAccessTokenWithInvalidPassword() {

        Optional<OIDCAccessTokenResponse> response = authenticationService.getAccessToken(userLogin, userPassword + "false");
        assertTrue(response.isEmpty());

    }


}