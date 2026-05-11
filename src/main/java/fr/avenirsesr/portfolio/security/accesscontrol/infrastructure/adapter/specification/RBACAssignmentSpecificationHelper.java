package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity_;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACContextEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACContextEntity_;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity_;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity_;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

/** Assignment specification helper. */
@Slf4j
public abstract class RBACAssignmentSpecificationHelper {

  public static Specification<RBACAssignmentEntity> filterByPrincipal(String login) {
    return (Root<RBACAssignmentEntity> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) -> {
      root.fetch(RBACAssignmentEntity_.principalEntity, JoinType.LEFT);
      root.fetch(RBACAssignmentEntity_.role, JoinType.LEFT);
      root.fetch(RBACAssignmentEntity_.scope, JoinType.LEFT);
      root.fetch(RBACAssignmentEntity_.context, JoinType.LEFT);

      query.distinct(true);

      return criteriaBuilder.equal(
          root.get(RBACAssignmentEntity_.principalEntity).get(PrincipalEntity_.login), login);
    };
  }

  public static Specification<RBACAssignmentEntity> filterByPrincipalAndResources(
      String login, UUID... resourceIds) {
    return (Root<RBACAssignmentEntity> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) -> {
      log.trace("filterByPrincipalAndResources, login: {}", login);
      log.trace("filterByPrincipalAndResources, resourceIds: {}", Arrays.toString(resourceIds));

      root.fetch(RBACAssignmentEntity_.principalEntity, JoinType.LEFT);
      root.fetch(RBACAssignmentEntity_.role, JoinType.LEFT);

      Join<RBACAssignmentEntity, RBACScopeEntity> joinScope =
          root.join(RBACAssignmentEntity_.scope, JoinType.INNER);
      Join<RBACScopeEntity, RBACResourceEntity> joinResource =
          joinScope.join(RBACScopeEntity_.resources, JoinType.INNER);

      query.distinct(true);

      return criteriaBuilder.and(
          criteriaBuilder.equal(
              root.get(RBACAssignmentEntity_.principalEntity).get(PrincipalEntity_.login), login),
          joinResource.get(RBACResourceEntity_.id).in((Object[]) resourceIds));
    };
  }

  public static Specification<RBACAssignmentEntity> filterByPrincipalContextAndResources(
      String login, RBACContextEntity executionContext, UUID... resourceIds) {
    return (Root<RBACAssignmentEntity> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) -> {
      if (log.isTraceEnabled()) {
        log.trace("filterByPrincipalContextAndResources, login: {}", login);
        log.trace("filterByPrincipalContextAndResources, executionContext: {}", executionContext);
        log.trace(
            "filterByPrincipalContextAndResources, resourceIds: {}", Arrays.toString(resourceIds));
      }

      root.fetch(RBACAssignmentEntity_.principalEntity, JoinType.LEFT);
      root.fetch(RBACAssignmentEntity_.role, JoinType.LEFT);

      Join<RBACAssignmentEntity, RBACScopeEntity> joinScope =
          root.join(RBACAssignmentEntity_.scope, JoinType.INNER);
      Join<RBACScopeEntity, RBACResourceEntity> joinResource =
          joinScope.join(RBACScopeEntity_.resources, JoinType.INNER);
      Join<RBACAssignmentEntity, RBACContextEntity> joinContext =
          root.join(RBACAssignmentEntity_.context, JoinType.INNER);

      Predicate principalPredicate =
          criteriaBuilder.equal(
              root.get(RBACAssignmentEntity_.principalEntity).get(PrincipalEntity_.login), login);

      Predicate resourcesPredicate =
          joinResource.get(RBACResourceEntity_.id).in((Object[]) resourceIds);

      Predicate contextDatePredicate =
          criteriaBuilder.and(
              criteriaBuilder.or(
                  criteriaBuilder.isNull(joinContext.get(RBACContextEntity_.validityStart)),
                  criteriaBuilder.lessThanOrEqualTo(
                      joinContext.get(RBACContextEntity_.validityStart),
                      executionContext.getEffectiveDate())),
              criteriaBuilder.or(
                  criteriaBuilder.isNull(joinContext.get(RBACContextEntity_.validityEnd)),
                  criteriaBuilder.greaterThanOrEqualTo(
                      joinContext.get(RBACContextEntity_.validityEnd),
                      executionContext.getEffectiveDate())));

      Predicate contextStructuresPredicate =
          criteriaBuilder.or(
              criteriaBuilder.isEmpty(joinContext.get(RBACContextEntity_.structureEntities)),
              joinContext
                  .join(RBACContextEntity_.structureEntities, JoinType.LEFT)
                  .in(executionContext.getStructureEntities()));

      query.distinct(true);

      return criteriaBuilder.and(
          principalPredicate, resourcesPredicate, contextDatePredicate, contextStructuresPredicate);
    };
  }
}
