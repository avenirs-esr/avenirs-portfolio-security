package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 *
 * <h1>RBACAssignmentRepositoryImpl</h1>
 *
 * <p>Description: RBACAssignmentRepositoryImpl is used for [describe the main functionality of the
 * class]. It provides [summarize key features or expected behavior].
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
 * 05/12/2024
 */
public class RBACAssignmentJpaRepositoryImpl implements RBACAssignmentJpaRepositoryCustom {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Save an assignment. The entities collections in scope and context are handled.
   *
   * @param assignment The assignment to save.
   * @return The persisted assignment.
   */
  public RBACAssignmentEntity saveWithRelations(RBACAssignmentEntity assignment) {

    List<RBACResourceEntity> managedResources =
        assignment.getScope().getResources().stream()
            .map(
                resource ->
                    entityManager.contains(resource) ? resource : entityManager.merge(resource))
            .toList();
    assignment.getScope().setResources(managedResources);

    Set<StructureEntity> managedStructureEntities =
        assignment.getContext().getStructureEntities().stream()
            .map(
                structure ->
                    entityManager.contains(structure) ? structure : entityManager.merge(structure))
            .collect(Collectors.toSet());
    assignment.getContext().setStructureEntities(managedStructureEntities);

    if (!entityManager.contains(assignment.getScope())) {
      assignment.setScope(entityManager.merge(assignment.getScope()));
    }
    if (!entityManager.contains(assignment.getContext())) {
      assignment.setContext(entityManager.merge(assignment.getContext()));
    }

    return entityManager.merge(assignment);
  }
}
