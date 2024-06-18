package fr.avenirsesr.portfolio.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import fr.avenirsesr.portfolio.security.repository.RoleRepository;

@SpringBootApplication
public class AvenirsPortfolioSecurityApplication {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AvenirsPortfolioSecurityApplication.class);
	
	@Autowired()
	private RoleRepository roleRepo;
	
	@EventListener(ApplicationReadyEvent.class)
	public void testRoleRepo(){
		LOGGER.warn("!!!!!!!! testRoleRepo !!!!!!!!");
		this.roleRepo.fetchAllRoles();
		
	}

	
	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(AvenirsPortfolioSecurityApplication.class, args);
		
		

	}
	
	public void foo() {}

}
