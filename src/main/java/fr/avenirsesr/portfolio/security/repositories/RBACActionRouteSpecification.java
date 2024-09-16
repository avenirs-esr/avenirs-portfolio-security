package fr.avenirsesr.portfolio.security.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public interface RBACActionRouteSpecification {

    /**
     * Specification to generate predicate to select the assignments associated to a
     * principal and a list of resources.
     * 
     * @param login       The login of the principal.
     * @param resourceIds The id of the resources.
     * @return The assignments for the principal and the resources.
     */

    public static Specification<RBACActionRoute> filterByURIAndMethod(String uri, String method) {
        final Logger LOGGER = LoggerFactory.getLogger(RBACActionRouteSpecification.class);
        return (Root<RBACActionRoute> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            LOGGER.trace("filterByURIAndMethod, uri: {}",uri);
            LOGGER.trace("filterByURIAndMethod, method: {}",method);

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(RBACActionRoute_.uri),uri),
                criteriaBuilder.equal(root.get(RBACActionRoute_.method),method)
            );
        };
    }
}
