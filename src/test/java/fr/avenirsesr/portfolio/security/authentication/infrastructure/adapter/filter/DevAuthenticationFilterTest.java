package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import jakarta.servlet.FilterChain;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class DevAuthenticationFilterTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_withoutEppnHeader_doesNotAuthenticate() throws Exception {
    BddLogger.given("a dev authentication filter");
    PrincipalRepository principalRepository = mock(PrincipalRepository.class);
    PrincipalGrantedAuthoritiesService authoritiesService =
        mock(PrincipalGrantedAuthoritiesService.class);
    DevAuthenticationFilter filter =
        new DevAuthenticationFilter(principalRepository, authoritiesService);

    BddLogger.and("a request without eppn header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verifyNoInteractions(principalRepository, authoritiesService);
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_withEppnHeaderMatchingAPrincipal_authenticatesWithItsLoginAndAuthorities()
      throws Exception {
    BddLogger.given("a dev authentication filter");
    PrincipalRepository principalRepository = mock(PrincipalRepository.class);
    PrincipalGrantedAuthoritiesService authoritiesService =
        mock(PrincipalGrantedAuthoritiesService.class);
    DevAuthenticationFilter filter =
        new DevAuthenticationFilter(principalRepository, authoritiesService);

    BddLogger.and("a request with an eppn header matching a known principal");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("eppn", "aya.germain@university.com");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.and("that principal's login and RBAC authorities");
    Principal principal =
        Principal.create(
            "aya.germain@university.com",
            "aya.germain",
            "OIDC",
            "aya.germain",
            EUserCategory.STAFF,
            EUserStatus.ACTIVE,
            Set.of());
    Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("rbac:assign"));
    when(principalRepository.findByEppn("aya.germain@university.com"))
        .thenReturn(Optional.of(principal));
    when(authoritiesService.loadAuthorities("aya.germain")).thenReturn(authorities);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should authenticate the user by its login, with its RBAC authorities");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals("aya.germain", authentication.getPrincipal());
    assertEquals(authorities, new java.util.HashSet<>(authentication.getAuthorities()));
  }

  @Test
  void doFilterInternal_withEppnHeaderMatchingNoPrincipal_authenticatesWithTheHeaderValue()
      throws Exception {
    BddLogger.given("a dev authentication filter");
    PrincipalRepository principalRepository = mock(PrincipalRepository.class);
    PrincipalGrantedAuthoritiesService authoritiesService =
        mock(PrincipalGrantedAuthoritiesService.class);
    DevAuthenticationFilter filter =
        new DevAuthenticationFilter(principalRepository, authoritiesService);

    BddLogger.and("a request with an eppn header matching no known principal");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("eppn", "deman");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    when(principalRepository.findByEppn("deman")).thenReturn(Optional.empty());
    when(authoritiesService.loadAuthorities("deman")).thenReturn(Set.of());

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should authenticate the user with the raw header value as login");
    verify(chain, times(1)).doFilter(request, response);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals("deman", authentication.getPrincipal());
  }

  @Test
  void doFilterInternal_withBlankEppnHeader_doesNotAuthenticate() throws Exception {
    BddLogger.given("a dev authentication filter");
    PrincipalRepository principalRepository = mock(PrincipalRepository.class);
    PrincipalGrantedAuthoritiesService authoritiesService =
        mock(PrincipalGrantedAuthoritiesService.class);
    DevAuthenticationFilter filter =
        new DevAuthenticationFilter(principalRepository, authoritiesService);

    BddLogger.and("a request with a blank eppn header");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("eppn", "   ");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    BddLogger.when("filtering the request");
    filter.doFilter(request, response, chain);

    BddLogger.then("it should not authenticate the user and should continue the filter chain");
    verifyNoInteractions(principalRepository, authoritiesService);
    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
