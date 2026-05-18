/** */
package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter.CASTokenAuthenticationFilter;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

  @Value("${management.actuator.health.path}")
  private String actuatorHealth;

  public SpringSecurityConfig(OidcService oidcService) {
    this.oidcService = oidcService;
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
  SecurityFilterChain protectedFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .securityMatcher("/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .addFilterBefore(casTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  Filter casTokenAuthenticationFilter() {
    return new CASTokenAuthenticationFilter(oidcService);
  }
}
