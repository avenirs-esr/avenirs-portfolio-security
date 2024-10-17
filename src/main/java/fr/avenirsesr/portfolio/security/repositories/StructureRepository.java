package fr.avenirsesr.portfolio.security.repositories;

import fr.avenirsesr.portfolio.security.models.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * <h1>StructureRepository</h1>
 * <p>
 * <b>Description:</b> Repository for Structure models.
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

public interface StructureRepository extends JpaRepository<Structure, Long>, JpaSpecificationExecutor<Structure> {
    /**
     * Find a structure by its name.
     * @param name The name of the structure to retrieve.
     * @return An Optional of Structure.
     */
    Optional<Structure> findByName(String name);

}
