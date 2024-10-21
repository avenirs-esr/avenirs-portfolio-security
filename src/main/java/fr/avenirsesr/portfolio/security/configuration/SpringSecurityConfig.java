/**
 *
 */
package fr.avenirsesr.portfolio.security.configuration;

import fr.avenirsesr.portfolio.security.filters.CASTokenAuthenticationFilter;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
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
 * Configuration for spring security.
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


    @Autowired
    private AuthenticationService authenticationService;


    @Bean
    SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/v3/**",
                        "/swagger-ui/**",
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








