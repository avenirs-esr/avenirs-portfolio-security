package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionRouteEntity;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {RBACActionMapper.class})
public interface RBACActionRouteMapper {

  RBACActionRoute toDomain(RBACActionRouteEntity entity);

  RBACActionRouteEntity fromDomain(RBACActionRoute domain);
}
