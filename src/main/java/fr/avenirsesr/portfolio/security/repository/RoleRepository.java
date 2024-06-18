/**
 * 
 */
package fr.avenirsesr.portfolio.security.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

/**
 * Repository for the roles 
 */
@Repository
public class RoleRepository {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleRepository.class);
	
	@Value("${avenirs.accessControl.roles.url}")
	private String rolesURL;
	
	/** Rest client to interact with storage module. */
	private RestClient restClient = RestClient.create();
	
	
	public void fetchAllRoles(){
		
		
		String roles = restClient.get()
		.uri(rolesURL)
		.retrieve()
		.body(String.class);
		
		LOGGER.warn("RoleRepository: " + roles);
		
	}
	
}
