package fr.avenirsesr.portfolio.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.models.RBACContext;
import fr.avenirsesr.portfolio.security.models.RBACRole;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.repositories.AssignmentSpecification;
import fr.avenirsesr.portfolio.security.services.AssignmentService;
import jakarta.transaction.Transactional;

/**
 * Avenirs Portfolio security module.
 */
@SpringBootApplication
public class AvenirsPortfolioSecurityApplication   implements CommandLineRunner {
	
	@Autowired 
	private AssignmentService assignmentService;

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AvenirsPortfolioSecurityApplication.class);
	
		public static void main(String[] args) {
			LOGGER.info("Starting Avenirs Portfolio Securiy Module");
		/*ApplicationContext app = */SpringApplication.run(AvenirsPortfolioSecurityApplication.class, args);
	}
		
		
		@Transactional
		@Override
		public void run(String... args) throws Exception {
			LOGGER.info("AvenirsPortfolioStorageApplication runing");
			//assignmentService.getAllAssignmentsByPredicate(AssignmentSpecification.assignmentHasPrincipalWithLogin("deman")).forEach(a-> LOGGER.debug("    Assignment:  " + a));
//			Principal p = new Principal();
//			p.setId(12);
//			
//			RBACRole r = new RBACRole();
//			r.setId(27);
//			
//			RBACScope s = new RBACScope();
//			s.setId(14);
//			
//			RBACContext c = new RBACContext();
//			c.setId(6);
//			
//			Assignment assignement = new Assignment(r,p,s, c);
//			
//			assignement =	assignmentService.createAssignment(assignement);
			LOGGER.trace("run, after query");
//			assignmentService.getAllAssignments().forEach(a-> LOGGER.debug("Assignment:  " + a));
			
		}
		
}
