package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *
 * <h1>StructureRepository</h1>
 *
 * <p><b>Description:</b> Repository for Structure models.
 *
 * <h2>Version:</h2>
 *
 * 1.0.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 17/10/2024
 */
public interface StructureJpaRepository
    extends JpaRepository<StructureEntity, UUID>, JpaSpecificationExecutor<StructureEntity> {
  /**
   * Find a structure by its name.
   *
   * @param name The name of the structure to retrieve.
   * @return An Optional of Structure.
   */
  Optional<StructureEntity> findByName(String name);
}
