package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RBACResourceTypeMapper {

  RBACResourceType toDomain(RBACResourceTypeEntity entity);

  RBACResourceTypeEntity fromDomain(RBACResourceType domain);
}
