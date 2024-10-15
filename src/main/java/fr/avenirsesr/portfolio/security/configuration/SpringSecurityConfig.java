/**
 * 
 */
package fr.avenirsesr.portfolio.security.configuration;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	@Value("${avenirs.access.control.roles}")
	private String roles;

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
		log.debug("filterChain, Configuring Spring security");
		log.debug("filterChain, Permit All for:")	;	
		log.debug("filterChain, Permit All for roles: {}", roles);	
		log.debug("filterChain, Permit All for oidcCallback: {}", oidcCallback);	
		log.debug("filterChain, Permit All for oidcRedirect: {}", oidcRedirect);	
		auth.requestMatchers(roles).permitAll();
		auth.requestMatchers(oidcCallback).permitAll();
		auth.requestMatchers(oidcRedirect).permitAll();
		auth.anyRequest().permitAll();
//		auth.anyRequest().authenticated();
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








