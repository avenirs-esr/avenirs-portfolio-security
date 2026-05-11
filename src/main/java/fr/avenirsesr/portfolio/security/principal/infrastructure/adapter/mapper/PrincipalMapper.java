package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PrincipalMapper {

  @Mapping(
      source = "structureEntities",
      target = "structures",
      qualifiedByName = "toDomainStructures")
  Principal toDomain(PrincipalEntity entity);

  @Mapping(
      source = "structures",
      target = "structureEntities",
      qualifiedByName = "toEntityStructures")
  PrincipalEntity fromDomain(Principal domain);

  @Named("toDomainStructures")
  default Set<Structure> toDomainStructures(Set<StructureEntity> entities) {
    if (entities == null) {
      return Collections.emptySet();
    }

    return entities.stream()
        .map(entity -> new Structure(entity.getId(), entity.getName(), entity.getDescription()))
        .collect(Collectors.toSet());
  }

  @Named("toEntityStructures")
  default Set<StructureEntity> toEntityStructures(Set<Structure> domains) {
    if (domains == null) {
      return Collections.emptySet();
    }

    return domains.stream()
        .map(
            domain ->
                new StructureEntity()
                    .setId(domain.id())
                    .setName(domain.name())
                    .setDescription(domain.description()))
        .collect(Collectors.toSet());
  }
}
