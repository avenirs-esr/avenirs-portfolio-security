package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper.PrincipalMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
      RBACRoleMapper.class,
      RBACScopeMapper.class,
      RBACContextMapper.class,
      PrincipalMapper.class
    })
public interface RBACAssignmentMapper {

  RBACAssignment toDomain(RBACAssignmentEntity entity);

  RBACAssignmentEntity fromDomain(RBACAssignment domain);
}
