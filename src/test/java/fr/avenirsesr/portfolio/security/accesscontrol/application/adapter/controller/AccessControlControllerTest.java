package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlGrantResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeRequestDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.AccessControlRevokeResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantResult;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeResult;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.AccessControlService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AccessControlControllerTest {

  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID STRUCTURE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ASSIGNMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ACTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final String LOGIN = "user1234";

  @Mock private AccessControlService service;

  @InjectMocks private AccessControlController controller;

  @Nested
  class GivenAccessControlController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an access control controller");
    }

    @Nested
    class WhenGrantingAccess {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("granting access");
      }

      @Nested
      class AndTheServiceGrantsAccessSuccessfully {
        private ResponseEntity<AccessControlGrantResponseDTO> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the service grants access successfully");

          when(service.grantAccess(any(AccessControlGrantCommand.class)))
              .thenReturn(new AccessControlGrantResult(LOGIN, true, ASSIGNMENT_ID, null));

          response = controller.grantAccess(grantRequest());
        }

        @Test
        void thenItShouldReturnOkWithGrantedResponse() {
          BddLogger.then("it should return OK with granted response");

          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertNotNull(response.getBody());
          assertTrue(response.getBody().isGranted());
          assertEquals(LOGIN, response.getBody().getLogin());
          assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
        }
      }

      @Nested
      class AndTheServiceThrowsAnError {
        private ResponseEntity<AccessControlGrantResponseDTO> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the service throws an error");

          when(service.grantAccess(any(AccessControlGrantCommand.class)))
              .thenThrow(new RuntimeException("Error during access granting"));

          response = controller.grantAccess(grantRequest());
        }

        @Test
        void thenItShouldReturnForbiddenWithErrorResponse() {
          BddLogger.then("it should return FORBIDDEN with error response");

          assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
          assertNotNull(response.getBody());
          assertFalse(response.getBody().isGranted());
          assertEquals(LOGIN, response.getBody().getLogin());
          assertEquals("Error during access granting", response.getBody().getError());
        }
      }
    }

    @Nested
    class WhenRevokingAccess {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("revoking access");
      }

      @Nested
      class AndTheServiceRevokesAccessSuccessfully {
        private ResponseEntity<AccessControlRevokeResponseDTO> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the service revokes access successfully");

          when(service.revokeAccess(any(AccessControlRevokeCommand.class)))
              .thenReturn(new AccessControlRevokeResult(LOGIN, true, ASSIGNMENT_ID, null));

          response = controller.revokeAccess(revokeRequest());
        }

        @Test
        void thenItShouldReturnOkWithRevokedResponse() {
          BddLogger.then("it should return OK with revoked response");

          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertNotNull(response.getBody());
          assertTrue(response.getBody().isRevoked());
          assertEquals(LOGIN, response.getBody().getLogin());
          assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
        }
      }

      @Nested
      class AndTheServiceThrowsAnError {
        private ResponseEntity<AccessControlRevokeResponseDTO> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the service throws an error");

          when(service.revokeAccess(any(AccessControlRevokeCommand.class)))
              .thenThrow(new RuntimeException("Error during access revoking"));

          response = controller.revokeAccess(revokeRequest());
        }

        @Test
        void thenItShouldReturnForbiddenWithErrorResponse() {
          BddLogger.then("it should return FORBIDDEN with error response");

          assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
          assertNotNull(response.getBody());
          assertFalse(response.getBody().isRevoked());
          assertEquals(LOGIN, response.getBody().getLogin());
          assertEquals(ASSIGNMENT_ID, response.getBody().getAssignmentId());
          assertEquals("Error during access revoking", response.getBody().getError());
        }
      }
    }

    @Nested
    class WhenCheckingAuthorization {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking authorization");
      }

      @Nested
      class AndTheAccessIsGranted {
        private ResponseEntity<Boolean> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the access is granted");

          when(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID)).thenReturn(true);

          response = controller.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnOkWithTrueBody() {
          BddLogger.then("it should return OK with true body");

          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertEquals(true, response.getBody());
        }
      }

      @Nested
      class AndTheAccessIsDenied {
        private ResponseEntity<Boolean> response;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the access is denied");

          when(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID)).thenReturn(false);

          response = controller.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnForbiddenWithFalseBody() {
          BddLogger.then("it should return FORBIDDEN with false body");

          assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
          assertEquals(false, response.getBody());
        }
      }
    }
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
