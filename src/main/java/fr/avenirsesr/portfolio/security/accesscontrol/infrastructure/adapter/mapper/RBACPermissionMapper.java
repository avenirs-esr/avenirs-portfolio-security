package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACPermissionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RBACPermissionMapper {

  RBACPermission toDomain(RBACPermissionEntity entity);

  RBACPermissionEntity fromDomain(RBACPermission domain);
}
