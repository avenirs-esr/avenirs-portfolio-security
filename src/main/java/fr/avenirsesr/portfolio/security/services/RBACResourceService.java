package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.models.RBACResource;
import fr.avenirsesr.portfolio.security.models.Structure;
import fr.avenirsesr.portfolio.security.repositories.RBACResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * <h1>RBACResourceService</h1>
 * <p>
 * <b>Description:</b> Service used to manage resources.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 17/10/2024
 */

@Slf4j
@Service
public class RBACResourceService {
	
	@Autowired
	private RBACResourceRepository resourceRepository;

	/**
	 * Gives a specific resource by its id.
	 * @param resourceId The id of the resource to retrieve.
	 * @return The Optional of resource with id resourceId.
	 */
	@Transactional(readOnly = true)
	public Optional<RBACResource> getResourceById(final Long resourceId){
		log.trace("getResourceById resourceId: {}", resourceId);
		return this.resourceRepository.findById(resourceId);
	}
	
	/**
	 * Gives all the resources.
	 * @return All the resources.
	 */
	@Transactional(readOnly = true)
	public List<RBACResource> getAllResources() {
		log.trace("getAllResources");
		return this.resourceRepository.findAll();
	}


	/**
	 * Gives all the resources associated to a specification.
	 *
	 * @param specification The specification used to filter the resources.
	 * @return The filtered resources.
	 */
	@Transactional(readOnly = true)
	public List<RBACResource> getAllResourcesBySpecification(Specification<RBACResource> specification) {
		log.trace("getAllResourcesBySpecification, specification: {}", specification);
		return resourceRepository.findAll(specification);
	}
	
	/**
	 * Creates a resource.
	 * @param resource The resource to create.
	 * @return The new created resource.
	 */
	@Transactional
	public RBACResource createResource(RBACResource resource) {
		log.trace("createResource, resource: {}", resource);
		return this.resourceRepository.save(resource);
	}
	
	/**
	 * Updates a resource.
	 * @param resource The resource to update.
	 * @return The updated resource.
	 */
	@Transactional
	public RBACResource  updateResource(RBACResource resource) {
		log.trace("updateResource, resource: {}", resource);
		return this.resourceRepository.findById(resource.getId())
				.map(storedResource -> {
					if (! storedResource.equals(resource)) {
                        storedResource.setSelector(resource.getSelector());
					}
					return storedResource;
				})
				.orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + resource.getId()));
	}
	
	/**
	 * Deletes a resource
	 * @param resourceId The id of the resource to delete.
	 */
	@Transactional
	public void deleteResource(Long resourceId) {
			log.trace("deleteResource, resourceId: {}", resourceId);
		this.resourceRepository.deleteById(resourceId);
	}
}
