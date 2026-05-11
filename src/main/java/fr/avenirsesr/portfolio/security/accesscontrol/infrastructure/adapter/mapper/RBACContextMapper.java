package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACContextEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper.StructureMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {StructureMapper.class})
public interface RBACContextMapper {

  RBACContext toDomain(RBACContextEntity entity);

  RBACContextEntity fromDomain(RBACContext domain);
}
