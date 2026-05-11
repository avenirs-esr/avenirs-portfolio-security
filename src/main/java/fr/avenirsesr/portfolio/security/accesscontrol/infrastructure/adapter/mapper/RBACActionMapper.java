package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RBACActionMapper {

  RBACAction toDomain(RBACActionEntity entity);

  RBACActionEntity fromDomain(RBACAction domain);
}
