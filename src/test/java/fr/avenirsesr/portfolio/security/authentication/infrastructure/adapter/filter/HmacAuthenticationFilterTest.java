package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import jakarta.servlet.FilterChain;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class HmacAuthenticationFilterTest {

  private static final String SECRET = "test-hmac-secret";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_withoutHeaders_doesNotAuthenticate() throws Exception {
    BddLogger.given("an HMAC authentication filter");
    HmacAuthenticationFilter filter = new HmacAuthenticationFilter(SECRET);

    BddLogger.and("a request without signed context headers");
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_withValidSignedContext_authenticatesWithAuthoritiesFromThePayload()
      throws Exception {
    BddLogger.given("an HMAC authentication filter");
    HmacAuthenticationFilter filter = new HmacAuthenticationFilter(SECRET);

    BddLogger.and("a request with a validly signed context carrying authorities");
    long now = Instant.now().getEpochSecond();
    String payload = payload("deman", now, now + 300, Set.of("rbac:assign", "rbac:revoke"));
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(AvenirsSecurityHeaders.SIGNED_CONTEXT, payload);
    request.addHeader(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, sign(payload));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should authenticate the user with the authorities carried by the payload");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals("deman", authentication.getPrincipal());
    assertEquals(
        Set.of("rbac:assign", "rbac:revoke"),
        authentication.getAuthorities().stream()
            .map(Object::toString)
            .collect(java.util.stream.Collectors.toSet()));
  }

  @Test
  void doFilterInternal_withInvalidSignature_doesNotAuthenticate() throws Exception {
    BddLogger.given("an HMAC authentication filter");
    HmacAuthenticationFilter filter = new HmacAuthenticationFilter(SECRET);

    BddLogger.and("a request with a signed context and a wrong signature");
    long now = Instant.now().getEpochSecond();
    String payload = payload("deman", now, now + 300, Set.of("rbac:assign"));
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(AvenirsSecurityHeaders.SIGNED_CONTEXT, payload);
    request.addHeader(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, "not-the-right-signature");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_withExpiredPayload_doesNotAuthenticate() throws Exception {
    BddLogger.given("an HMAC authentication filter");
    HmacAuthenticationFilter filter = new HmacAuthenticationFilter(SECRET);

    BddLogger.and("a request with a validly signed but expired context");
    long now = Instant.now().getEpochSecond();
    String payload = payload("deman", now - 600, now - 300, Set.of("rbac:assign"));
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(AvenirsSecurityHeaders.SIGNED_CONTEXT, payload);
    request.addHeader(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, sign(payload));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  private String payload(String sub, long iat, long exp, Set<String> authorities) throws Exception {
    return objectMapper.writeValueAsString(new SignedPayload(sub, iat, exp, authorities));
  }

  private String sign(String payload) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return Base64.getEncoder()
        .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }

  private record SignedPayload(String sub, long iat, long exp, Set<String> authorities) {}
}
