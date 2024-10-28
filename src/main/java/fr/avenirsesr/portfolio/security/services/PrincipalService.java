package fr.avenirsesr.portfolio.security.services;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.model.Principal;
import fr.avenirsesr.portfolio.security.repositories.PrincipalRepository;


/**
 * Service for Principal.
 */

@Slf4j
@Service
public class PrincipalService {
	
	@Autowired
	private PrincipalRepository principalRepository;
	
	
	/**
	 * Gives all the principals.
	 * @return All the principals.
	 */
	public List<Principal> getAllPrincipals() {
		log.trace("getAllPrincipals");
		return this.principalRepository.findAll();
	}
	
	/**
	 * Gives a Principal associated to a login.
	 * @return The principal.
	 */
	public Optional<Principal> getPrincipalByLogin(String login) {
		log.trace("getPrincipalByLogin, login: {}", login);
		return this.principalRepository.findByLogin(login);
	}

}
