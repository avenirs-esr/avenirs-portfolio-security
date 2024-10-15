package fr.avenirsesr.portfolio.security.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.models.RBACActionRoute_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Assignment specification for ActionRoute model. Used to make queries based on
 * predicates.
 */
@Slf4j
public abstract class RBACActionRouteSpecificationHelper {

    /**
     * Specification to generate predicate to select the action associated to an uri and a method (http).
     * @param uri  The uri used to retrieve the action.
     * @param method The HTTP method used to retrieve the action.
     * @return An instance of RBACActionRoute specification.
     */

    public static Specification<RBACActionRoute> filterByURIAndMethod(String uri, String method) {
        return (Root<RBACActionRoute> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            log.trace("filterByURIAndMethod, uri: {}",uri);
            log.trace("filterByURIAndMethod, method: {}",method);

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(RBACActionRoute_.uri),uri),
                criteriaBuilder.equal(root.get(RBACActionRoute_.method),method)
            );
        };
    }
}