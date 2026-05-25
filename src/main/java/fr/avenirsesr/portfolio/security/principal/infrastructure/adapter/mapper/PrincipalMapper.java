package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.StructureEntity;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PrincipalMapper implements Mapper<PrincipalEntity, Principal> {

  public static final PrincipalMapper INSTANCE = new PrincipalMapper();

  @Override
  public PrincipalEntity fromDomain(Principal principal) {
    return principal != null
        ? PrincipalEntity.of(
            principal.getId(),
            principal.getEppn(),
            principal.getLogin(),
            null,
            principal.getProvider(),
            principal.getExternalId(),
            principal.getCategory(),
            principal.getStatus(),
            toEntityStructures(principal.getStructures()),
            principal.getCreatedAt(),
            principal.getUpdatedAt())
        : null;
  }

  @Override
  public Principal toDomain(PrincipalEntity principalEntity) {
    return principalEntity != null
        ? Principal.toDomain(
            principalEntity.getId(),
            principalEntity.getCreatedAt(),
            principalEntity.getUpdatedAt(),
            principalEntity.getEppn(),
            principalEntity.getLogin(),
            principalEntity.getProvider(),
            principalEntity.getExternalId(),
            principalEntity.getCategory(),
            principalEntity.getStatus(),
            toDomainStructures(principalEntity.getStructureEntities()))
        : null;
  }

  private Set<Structure> toDomainStructures(Set<StructureEntity> entities) {
    if (entities == null) {
      return Collections.emptySet();
    }

    return entities.stream()
        .map(entity -> new Structure(entity.getId(), entity.getName(), entity.getDescription()))
        .collect(Collectors.toSet());
  }

  private Set<StructureEntity> toEntityStructures(Set<Structure> domains) {
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
