package fr.avenirsesr.portfolio.security.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACRole;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRepository;
import fr.avenirsesr.portfolio.security.repositories.RBACRoleRepository;


/**
 * Action Service
 */
@Service
public class RBACActionService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACActionService.class);
	
	@Autowired
	private RBACActionRepository actionRepository;
	
	/**
	 * Gives a specific action.
	 * @param actionId The id of the role to retrieve.
	 * @return The action with id actionId.
	 */
	public RBACAction getAction(final Long actionId){
		LOGGER.trace("getAction");
		return this.actionRepository.findById(actionId).get();
	}
	
	/**
	 * Gives all the actions.
	 * @return All the action.
	 */
	public Iterable<RBACAction> getAllActions() {
		LOGGER.trace("getAllActions");
		return this.actionRepository.findAll();
	}
	
		
}
