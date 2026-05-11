package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.RoleResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;

public final class RoleDTOMapper {

  private RoleDTOMapper() {}

  public static RoleResponseDTO fromDomain(RBACRole role) {
    return new RoleResponseDTO(role.id(), role.name(), role.description());
  }
}
