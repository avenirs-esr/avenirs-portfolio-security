/**
 * 
 */
package fr.avenirsesr.portfolio.security.configuration;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		//return httpSecurity.build();
		return httpSecurity.authorizeHttpRequests(auth -> { 
		auth.requestMatchers("/admin").hasRole("ADMIN");
		auth.requestMatchers("/user").hasRole("USER");
		auth.anyRequest().authenticated();
		}).formLogin(Customizer.withDefaults()).build();
	}
    
    @Bean
    UserDetailsService users() {
    	UserDetails user = User.builder()
    			.username("deman")
    			.password(passwordEncoder().encode("user"))
    			.roles("USER")
    			.build();
    	UserDetails admin = User.builder()
    			.username("admin")
    			.password(passwordEncoder().encode("admin"))
    			.roles("USER", "ADMIN")
    			.build();
    	
    	return new InMemoryUserDetailsManager(user, admin);
    }
    
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticaionManager(HttpSecurity httpSecurity, BCryptPasswordEncoder bCryptPasswordEndoder) 
    throws Exception {
    	AuthenticationManagerBuilder authenticationManagerBuilder =  httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
    	authenticationManagerBuilder
    		.userDetailsService(userDetailsService)
    		.passwordEncoder(bCryptPasswordEndoder);
    	return authenticationManagerBuilder.build();
    }
}








