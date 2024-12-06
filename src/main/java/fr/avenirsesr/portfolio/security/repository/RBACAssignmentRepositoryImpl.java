package fr.avenirsesr.portfolio.security.repository;


import fr.avenirsesr.portfolio.security.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.model.RBACResource;
import fr.avenirsesr.portfolio.security.model.Structure;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>RBACAssignmentRepositoryImpl</h1>
 * <p>
 * Description: RBACAssignmentRepositoryImpl is used for [describe the main functionality of the class].
 * It provides [summarize key features or expected behavior].
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 05/12/2024
 */
public class RBACAssignmentRepositoryImpl implements RBACAssignmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save an assignment. The entities collections in scope and context are handled.
     * @param assignment The assignment to save.
     * @return The persisted assignment.
     */
    public RBACAssignment saveWithRelations(RBACAssignment assignment) {

        List<RBACResource> managedResources = assignment.getScope().getResources().stream()
                .map(resource -> entityManager.contains(resource) ? resource : entityManager.merge(resource))
                .toList();
        assignment.getScope().setResources(managedResources);

        Set<Structure> managedStructures = assignment.getContext().getStructures().stream()
                .map(structure -> entityManager.contains(structure) ? structure : entityManager.merge(structure))
                .collect(Collectors.toSet());
        assignment.getContext().setStructures(managedStructures);

        if (!entityManager.contains(assignment.getScope())) {
            assignment.setScope(entityManager.merge(assignment.getScope()));
        }
        if (!entityManager.contains(assignment.getContext())) {
            assignment.setContext(entityManager.merge(assignment.getContext()));
        }

        return entityManager.merge(assignment);
    }
}