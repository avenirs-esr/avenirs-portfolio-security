package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StructureMapper {

  Structure toDomain(StructureEntity entity);

  StructureEntity fromDomain(Structure domain);
}
