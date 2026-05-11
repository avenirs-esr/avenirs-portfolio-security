package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACRoleEntity;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {RBACPermissionMapper.class})
public interface RBACRoleMapper {

  RBACRole toDomain(RBACRoleEntity entity);

  RBACRoleEntity fromDomain(RBACRole domain);
}
