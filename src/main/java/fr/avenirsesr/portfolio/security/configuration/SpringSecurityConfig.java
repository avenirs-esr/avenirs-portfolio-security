/**
 * 
 */
package fr.avenirsesr.portfolio.security.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration for spring security.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityConfig.class);

	/** OIDC callback URI.*/
	@Value("${avenirs.authentication.oidc.callback}")
	private String oidcCallback;
	
	/** OIDC callback redirect URI.*/
	@Value("${avenirs.authentication.oidc.callback.redirect}")
	private String oidcRedirect;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> { 
		LOGGER.info("filterChain, Configuring Spring security");
		LOGGER.info("filterChain, Permit All for authentication callbacks:")	;	
		LOGGER.info("filterChain, oidcCallback: " + oidcCallback);	
		LOGGER.info("filterChain, oidcRedirect: " + oidcRedirect);	
		auth.requestMatchers(oidcCallback).permitAll();
		auth.requestMatchers(oidcRedirect).permitAll();
		auth.anyRequest().authenticated();
		}).formLogin(Customizer.withDefaults()).build();
	}
    
//    @Bean
//    UserDetailsService users() {
//    	UserDetails user = User.builder()
//    			.username("deman")
//    			.password(passwordEncoder().encode("user"))
//    			.roles("USER")
//    			.build();
//    	UserDetails admin = User.builder()
//    			.username("admin")
//    			.password(passwordEncoder().encode("admin"))
//    			.roles("USER", "ADMIN")
//    			.build();
//    	
//    	return new InMemoryUserDetailsManager(user, admin);
//    }
    
//    @Bean
//    BCryptPasswordEncoder passwordEncoder() {
//    	return new BCryptPasswordEncoder();
//    }
    
//    @Bean
//    AuthenticationManager authenticaionManager(HttpSecurity httpSecurity, BCryptPasswordEncoder bCryptPasswordEndoder) 
//    throws Exception {
//    	AuthenticationManagerBuilder authenticationManagerBuilder =  httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
//    	authenticationManagerBuilder
//    		.userDetailsService(userDetailsService)
//    		.passwordEncoder(bCryptPasswordEndoder);
//    	return authenticationManagerBuilder.build();
//    }
}








