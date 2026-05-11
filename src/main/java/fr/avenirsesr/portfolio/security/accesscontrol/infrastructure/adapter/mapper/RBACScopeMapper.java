package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {RBACResourceMapper.class})
public interface RBACScopeMapper {

  RBACScope toDomain(RBACScopeEntity entity);

  RBACScopeEntity fromDomain(RBACScope domain);
}
