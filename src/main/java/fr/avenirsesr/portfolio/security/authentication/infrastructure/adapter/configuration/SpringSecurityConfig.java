/** */
package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter.CASTokenAuthenticationFilter;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter.DevAuthenticationFilter;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter.HmacAuthenticationFilter;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 *
 * <h1>SpringSecurityConfig</h1>
 *
 * <p><b>Description:</b> Configuration for spring security.
 *
 * <h2>Version:</h2>
 *
 * 1.0.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 07/11/2024
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

  private static final String LOGIN = "/login";

  /** OIDC callback URI. */
  @Value("${avenirs.authentication.oidc.callback}")
  private String oidcCallback;

  /** OIDC callback redirect URI. */
  @Value("${avenirs.authentication.oidc.callback.redirect}")
  private String oidcRedirect;

  /** Swagger API Doc path. */
  @Value("${springdoc.api-docs.path}")
  private String swaggerAPIDocPath;

  /** Swagger UI path. */
  @Value("${springdoc.swagger-ui.path}")
  private String swaggerUIPath;

  private final OidcService oidcService;
  private final PrincipalGrantedAuthoritiesService principalGrantedAuthoritiesService;
  private final PrincipalRepository principalRepository;

  @Value("${management.actuator.health.path}")
  private String actuatorHealth;

  @Value("${avenirs.access.control.grant}")
  private String accessControlGrantPath;

  @Value("${avenirs.access.control.revoke}")
  private String accessControlRevokePath;

  @Value("${security.authentication.filter:hmac}")
  private String accessControlFilterMode;

  @Value("${security.hmac.secret}")
  private String hmacSecret;

  public SpringSecurityConfig(
      OidcService oidcService,
      PrincipalGrantedAuthoritiesService principalGrantedAuthoritiesService,
      PrincipalRepository principalRepository) {
    this.oidcService = oidcService;
    this.principalGrantedAuthoritiesService = principalGrantedAuthoritiesService;
    this.principalRepository = principalRepository;
  }

  @Bean
  @Order(1)
  SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .securityMatcher(
            swaggerUIPath + "/**",
            swaggerAPIDocPath + "/**",
            LOGIN,
            actuatorHealth,
            oidcCallback,
            oidcRedirect,
            "/auth/**",
            "/internal/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain accessControlFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .securityMatcher(accessControlGrantPath, accessControlRevokePath)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .addFilterBefore(casTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            accessControlAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  @Order(3)
  SecurityFilterChain protectedFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .securityMatcher("/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .addFilterBefore(casTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  Filter accessControlAuthenticationFilter() {
    if ("dev".equalsIgnoreCase(accessControlFilterMode)) {
      log.warn(
          "access-control endpoints are running in DEV mode ({}), do not use in production",
          DevAuthenticationFilter.class.getSimpleName());
      return new DevAuthenticationFilter(principalRepository, principalGrantedAuthoritiesService);
    }
    return new HmacAuthenticationFilter(hmacSecret);
  }

  Filter casTokenAuthenticationFilter() {
    return new CASTokenAuthenticationFilter(oidcService, principalGrantedAuthoritiesService);
  }
}
