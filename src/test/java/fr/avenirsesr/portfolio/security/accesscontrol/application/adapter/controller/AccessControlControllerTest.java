package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.AccessControlService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AccessControlControllerTest {

  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID STRUCTURE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ASSIGNMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ACTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final String LOGIN = "user1234";

  @Test
  void grantAccessSuccess() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.grantAccess(any(AccessControlGrantCommand.class)))
        .thenReturn(new AccessControlGrantResult(LOGIN, true, ASSIGNMENT_ID, null));

    ResponseEntity<AccessControlGrantResponseDTO> response = controller.grantAccess(grantRequest());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isGranted());
    assertEquals(LOGIN, response.getBody().getLogin());
    assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
  }

  @Test
  void grantAccessWithServiceError() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.grantAccess(any(AccessControlGrantCommand.class)))
        .thenThrow(new RuntimeException("Error during access granting"));

    ResponseEntity<AccessControlGrantResponseDTO> response = controller.grantAccess(grantRequest());

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isGranted());
    assertEquals(LOGIN, response.getBody().getLogin());
    assertEquals("Error during access granting", response.getBody().getError());
  }

  @Test
  void revokeAccessSuccess() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.revokeAccess(any(AccessControlRevokeCommand.class)))
        .thenReturn(new AccessControlRevokeResult(LOGIN, true, ASSIGNMENT_ID, null));

    ResponseEntity<AccessControlRevokeResponseDTO> response =
        controller.revokeAccess(revokeRequest());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isRevoked());
    assertEquals(LOGIN, response.getBody().getLogin());
    assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
  }

  @Test
  void revokeAccessWithServiceError() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.revokeAccess(any(AccessControlRevokeCommand.class)))
        .thenThrow(new RuntimeException("Error during access revoking"));

    ResponseEntity<AccessControlRevokeResponseDTO> response =
        controller.revokeAccess(revokeRequest());

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isRevoked());
    assertEquals(LOGIN, response.getBody().getLogin());
    assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
    assertEquals("Error during access revoking", response.getBody().getError());
  }

  @Test
  void isAuthorizedReturnsOkWhenGranted() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID)).thenReturn(true);

    ResponseEntity<Boolean> response = controller.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  void isAuthorizedReturnsForbiddenWhenDenied() {
    AccessControlService service = mock(AccessControlService.class);
    AccessControlController controller = new AccessControlController(service);

    when(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID)).thenReturn(false);

    ResponseEntity<Boolean> response = controller.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  private AccessControlGrantRequestDTO grantRequest() {
    return new AccessControlGrantRequestDTO()
        .setLogin(LOGIN)
        .setRoleId(ROLE_ID)
        .setResourceIds(List.of(RESOURCE_ID))
        .setValidityStart("2024-10-01")
        .setValidityEnd("2024-12-31")
        .setStructureIds(List.of(STRUCTURE_ID));
  }

  private AccessControlRevokeRequestDTO revokeRequest() {
    return new AccessControlRevokeRequestDTO().setLogin(LOGIN).setAssignmentId(ASSIGNMENT_ID);
  }
}
