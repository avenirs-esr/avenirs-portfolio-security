package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.mapper.AccessControlDTOMapper;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.AccessControlService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/access-control")
public class AccessControlController {

  private final AccessControlService accessControlService;

  @PostMapping("/grant")
  public ResponseEntity<AccessControlGrantResponseDTO> grantAccess(
      @RequestBody AccessControlGrantRequestDTO request) {
    try {
      var result = accessControlService.grantAccess(AccessControlDTOMapper.toCommand(request));
      return ResponseEntity.ok(AccessControlDTOMapper.fromResult(result));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              new AccessControlGrantResponseDTO()
                  .setLogin(request.getLogin())
                  .setGranted(false)
                  .setError(e.getMessage()));
    }
  }

  @PostMapping("/revoke")
  public ResponseEntity<AccessControlRevokeResponseDTO> revokeAccess(
      @RequestBody AccessControlRevokeRequestDTO request) {
    try {
      var result = accessControlService.revokeAccess(AccessControlDTOMapper.toCommand(request));
      return ResponseEntity.ok(AccessControlDTOMapper.fromResult(result));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              new AccessControlRevokeResponseDTO()
                  .setLogin(request.getLogin())
                  .setRevoked(false)
                  .setAssignmentId(request.getAssignmentId())
                  .setError(e.getMessage()));
    }
  }

  @GetMapping("/authorize")
  public ResponseEntity<Boolean> isAuthorized(
      @RequestParam String login, @RequestParam UUID actionId, @RequestParam UUID resourceId) {
    boolean authorized = accessControlService.isAuthorized(login, actionId, resourceId);

    if (!authorized) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
    }

    return ResponseEntity.ok(true);
  }
}
