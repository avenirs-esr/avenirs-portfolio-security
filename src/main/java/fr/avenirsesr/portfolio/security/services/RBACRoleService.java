package fr.avenirsesr.portfolio.security.services;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACRole;
import fr.avenirsesr.portfolio.security.repositories.RBACRoleRepository;


/**
 * <h1>RBACRoleService</h1>
 * <p>
 * <b>Description:</b> RBAC Role Service.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 15/10/2024
 */

@Slf4j
@Service
public class RBACRoleService {
	
	@Autowired
	private RBACRoleRepository roleRepository;

	/**
	 * Gives a specific role by its id.
	 * @param roleId The id of the role to retrieve.
	 * @return The Optional of role with id roleId.
	 */
	public Optional<RBACRole> getRoleById(final Long roleId){
		log.trace("getRoleById roleId: {}", roleId);
		return this.roleRepository.findById(roleId);
	}

	/**
	 * Gives a specific role by its name.
	 * @param roleName The name of the role to retrieve.
	 * @return The Optional of role with name roleName.
	 */
	public Optional<RBACRole> getRoleByName(final String roleName) {
		log.trace("getRoleByName roleName: {}", roleName);
		return this.roleRepository.findByName(roleName);
	}
	
	/**
	 * Gives all the roles.
	 * @return All the roles.
	 */
	public List<RBACRole> getAllRoles() {
		log.trace("getAllRoles");
		return this.roleRepository.findAll();
	}
	
	/**
	 * Creates a role.
	 * @param role The role to create.
	 * @return The new created role.
	 */
	public RBACRole createRole(RBACRole role) {
		log.trace("createRole, role: {}", role);
		return this.roleRepository.save(role);
	}
	
	/**
	 * Updates a role.
	 * @param role The role to update.
	 * @return The updated role.
	 */
	public RBACRole  updateRole(RBACRole role) {
		log.trace("updateRole, role: {}", role);
		return this.roleRepository.findById(role.getId())
				.map(storedRole -> {
					storedRole.setName(role.getName());
					storedRole.setDescription(role.getDescription());
					return roleRepository.save(storedRole);
				})
				.orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + role.getId()));
	}
	
	/**
	 * Deletes a role
	 * @param roleId The id of the role to delete.
	 */
	public void deleteRole(Long roleId) {
			log.trace("deleteRole, roleId: {}", roleId);
		this.roleRepository.deleteById(roleId);
	}
}
