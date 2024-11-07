/**
 *
 */
package fr.avenirsesr.portfolio.security.configuration;

import fr.avenirsesr.portfolio.security.filter.CASTokenAuthenticationFilter;
import fr.avenirsesr.portfolio.security.service.AuthenticationService;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <h1>SpringSecurityConfig</h1>
 * <p>
 * <b>Description:</b> Configuration for spring security.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 07/11/2024
 */

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Value("${avenirs.authentication.oidc.login}")
    private String login;

    /**
     * OIDC callback URI.
     */
    @Value("${avenirs.authentication.oidc.callback}")
    private String oidcCallback;

    /**
     * OIDC callback redirect URI.
     */
    @Value("${avenirs.authentication.oidc.callback.redirect}")
    private String oidcRedirect;

    /**
     * Swagger API Doc path.
     */
    @Value("${springdoc.api-docs.path}")
    private String swaggerAPIDocPath;

    /**
     * Swagger UI path.
     */
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUIPath;

    @Autowired
    private AuthenticationService authenticationService;


    @Bean
    SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(swaggerUIPath + "/**",
                        swaggerAPIDocPath + "/**",
                        login,
                        oidcCallback,
                        oidcRedirect)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    SecurityFilterChain protectedFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(casTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    Filter casTokenAuthenticationFilter(){
        return new CASTokenAuthenticationFilter(authenticationService);
    }

}








