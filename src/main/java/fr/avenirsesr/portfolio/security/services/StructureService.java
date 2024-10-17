package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.models.Structure;
import fr.avenirsesr.portfolio.security.repositories.StructureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * <h1>StructureService</h1>
 * <p>
 * <b>Description:</b> Service used to manage Structures.
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
public class StructureService {
	
	@Autowired
	private StructureRepository structureRepository;

	/**
	 * Gives a specific structure by its id.
	 * @param structureId The id of the structure to retrieve.
	 * @return The Optional of structure with id structureId.
	 */
	@Transactional(readOnly = true)
	public Optional<Structure> getStructureById(final Long structureId){
		log.trace("getStructureById structureId: {}", structureId);
		return this.structureRepository.findById(structureId);
	}

	/**
	 * Gives a specific structure by its name.
	 * @param structureName The name of the structure to retrieve.
	 * @return The Optional of structure with name structureName.
	 */
	@Transactional(readOnly = true)
	public Optional<Structure> getStructureByName(final String structureName){
		log.trace("getStructureById structureId: {}", structureName);
		return this.structureRepository.findByName(structureName);
	}
	
	/**
	 * Gives all the structures.
	 * @return All the structures.
	 */
	@Transactional(readOnly = true)
	public List<Structure> getAllStructures() {
		log.trace("getAllStructures");
		return this.structureRepository.findAll();
	}

	/**
	 * Gives all the structures associated to a specification.
	 *
	 * @param specification The specification used to filter the strutures.
	 * @return The filtered structures.
	 */
	@Transactional(readOnly = true)
	public List<Structure> getAllStructuresBySpecification(Specification<Structure> specification) {
		log.trace("getAllStructuresBySpecification, specification: {}", specification);
		return structureRepository.findAll(specification);
	}
	
	/**
	 * Creates a structure.
	 * @param structure The structure to create.
	 * @return The new created structure.
	 */
	@Transactional
	public Structure createStructure(Structure structure) {
		log.trace("createStructure, structure: {}", structure);
		return this.structureRepository.save(structure);
	}
	
	/**
	 * Updates a structure.
	 * @param structure The structure to update.
	 * @return The updated structure.
	 */
	@Transactional
	public Structure  updateStructure(Structure structure) {
		log.trace("updateStructure, structure: {}", structure);
		return this.structureRepository.findById(structure.getId())
				.map(storedStructure -> {
					if (! storedStructure.equals(structure)) {
                        storedStructure.setName(structure.getName());
                        storedStructure.setDescription(structure.getDescription());
					}
					return storedStructure;
				})
				.orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + structure.getId()));
	}
	
	/**
	 * Deletes a structure
	 * @param structureId The id of the structure to delete.
	 */
	@Transactional
	public void deleteStructure(Long structureId) {
			log.trace("deleteStructure, structureId: {}", structureId);
		this.structureRepository.deleteById(structureId);
	}
}
