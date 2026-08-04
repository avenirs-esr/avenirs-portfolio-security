package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper.PrincipalMapper;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {RBACRoleMapper.class, RBACScopeMapper.class, RBACContextMapper.class})
public interface RBACAssignmentMapper {

  @Mapping(target = "principal", source = "principalEntity")
  RBACAssignment toDomain(RBACAssignmentEntity entity);

  @Mapping(target = "principalEntity", source = "principal")
  RBACAssignmentEntity fromDomain(RBACAssignment domain);

  default Principal map(PrincipalEntity entity) {
    return PrincipalMapper.INSTANCE.toDomain(entity);
  }

  default PrincipalEntity map(Principal principal) {
    return PrincipalMapper.INSTANCE.fromDomain(principal);
  }
}
