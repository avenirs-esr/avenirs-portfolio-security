package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.mapper;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantResult;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeResult;

public final class AccessControlDTOMapper {

  private AccessControlDTOMapper() {}

  public static AccessControlGrantCommand toCommand(AccessControlGrantRequestDTO dto) {
    return new AccessControlGrantCommand(
        dto.getLogin(),
        dto.getRoleId(),
        dto.getResourceIds(),
        dto.getValidityStart(),
        dto.getValidityEnd(),
        dto.getStructureIds());
  }

  public static AccessControlGrantResponseDTO fromResult(AccessControlGrantResult result) {
    return new AccessControlGrantResponseDTO()
        .setLogin(result.login())
        .setGranted(result.granted())
        .setAssignmentId(result.assignmentId())
        .setError(result.error());
  }

  public static AccessControlRevokeCommand toCommand(AccessControlRevokeRequestDTO dto) {
    return new AccessControlRevokeCommand(dto.getLogin(), dto.getAssignmentId());
  }

  public static AccessControlRevokeResponseDTO fromResult(AccessControlRevokeResult result) {
    return new AccessControlRevokeResponseDTO()
        .setLogin(result.login())
        .setRevoked(result.revoked())
        .setAssignmentId(result.assignmentId())
        .setError(result.error());
  }
}
