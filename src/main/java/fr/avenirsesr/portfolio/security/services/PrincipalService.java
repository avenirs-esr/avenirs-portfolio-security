package fr.avenirsesr.portfolio.security.services;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.repositories.PrincipalRepository;


/**
 * Service for Principal.
 */
@Service
public class PrincipalService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalService.class);
	
	@Autowired
	private PrincipalRepository principalRepository;
	
	
	/**
	 * Gives all the principals.
	 * @return All the principals.
	 */
	public List<Principal> getAllPrincipals() {
		LOGGER.trace("getAllPrincipals");
		return this.principalRepository.findAll();
	}
	
	/**
	 * Gives a Principal associated to a login.
	 * @return The principal.
	 */
	public Optional<Principal> getPrincipalByLogin(String login) {
		LOGGER.trace("getPrincipalByLogin, login: {}", login);
		return this.principalRepository.findByLogin(login);
	}

}
