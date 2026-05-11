package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RBACResourceTypeMapper.class)
public interface RBACResourceMapper {

  RBACResource toDomain(RBACResourceEntity entity);

  RBACResourceEntity fromDomain(RBACResource domain);
}
