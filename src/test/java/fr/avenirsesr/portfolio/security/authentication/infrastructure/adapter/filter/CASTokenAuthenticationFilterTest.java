package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class CASTokenAuthenticationFilterTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_withoutToken_doesNotAuthenticate() throws Exception {
    BddLogger.given("a CAS token authentication filter");
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    CASTokenAuthenticationFilter filter = new CASTokenAuthenticationFilter(authenticationService);

    BddLogger.and("a request without Authorization header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then(
        "it should not call the authentication service and should not authenticate the user");
    verifyNoInteractions(authenticationService);
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);
  }

  @Test
  void doFilterInternal_withActiveToken_setsAuthentication() throws Exception {
    BddLogger.given("a CAS token authentication filter");
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    CASTokenAuthenticationFilter filter = new CASTokenAuthenticationFilter(authenticationService);

    BddLogger.and("a request with an Authorization header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer TEST_ACCESS_TOKEN");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.and("an introspect response indicating the token is active");
    OIDCIntrospection introspection = new OIDCIntrospection(null, true, "deman", null);

    when(authenticationService.introspectAccessToken("TEST_ACCESS_TOKEN"))
        .thenReturn(introspection);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should authenticate the user and continue the filter chain");
    verify(authenticationService, times(1)).introspectAccessToken("TEST_ACCESS_TOKEN");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals("deman", authentication.getPrincipal());
    assertEquals("TEST_ACCESS_TOKEN", authentication.getCredentials());
  }

  @Test
  void doFilterInternal_withInactiveToken_doesNotSetAuthentication() throws Exception {
    BddLogger.given("a CAS token authentication filter");
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    CASTokenAuthenticationFilter filter = new CASTokenAuthenticationFilter(authenticationService);

    BddLogger.and("a request with an Authorization header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer TEST_ACCESS_TOKEN");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.and("an introspect response indicating the token is inactive");
    OIDCIntrospection introspection = new OIDCIntrospection(null, false, null, null);

    when(authenticationService.introspectAccessToken("TEST_ACCESS_TOKEN"))
        .thenReturn(introspection);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verify(authenticationService, times(1)).introspectAccessToken("TEST_ACCESS_TOKEN");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);
  }

  @Test
  void doFilterInternal_withXAuthorizationHeader_setsAuthentication() throws Exception {
    BddLogger.given("a CAS token authentication filter");
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    CASTokenAuthenticationFilter filter = new CASTokenAuthenticationFilter(authenticationService);

    BddLogger.and("a request with an x-authorization header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("x-authorization", "TEST_ACCESS_TOKEN");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.and("an introspect response indicating the token is active");
    OIDCIntrospection introspection = new OIDCIntrospection(null, true, "deman", null);
    when(authenticationService.introspectAccessToken("TEST_ACCESS_TOKEN"))
        .thenReturn(introspection);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should authenticate the user and continue the filter chain");
    verify(authenticationService, times(1)).introspectAccessToken("TEST_ACCESS_TOKEN");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals("deman", authentication.getPrincipal());
    assertEquals("TEST_ACCESS_TOKEN", authentication.getCredentials());
  }

  @Test
  void doFilterInternal_withNonBearerOrEmptyBearerToken_doesNotAuthenticate() throws Exception {
    BddLogger.given("a CAS token authentication filter");
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    CASTokenAuthenticationFilter filter = new CASTokenAuthenticationFilter(authenticationService);

    BddLogger.and("a request with a non-bearer authorization header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Basic abcdef");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then(
        "it should not call the authentication service and should not authenticate the user");
    verifyNoInteractions(authenticationService);
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());

    BddLogger.and("a request with an empty bearer token");
    SecurityContextHolder.clearContext();
    MockHttpServletRequest request2 = new MockHttpServletRequest();
    request2.addHeader("Authorization", "Bearer   ");
    FilterChain chain2 = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request2, response, chain2);

    BddLogger.then(
        "it should not call the authentication service and should not authenticate the user");
    verifyNoInteractions(authenticationService);
    verify(chain2, times(1)).doFilter(request2, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
