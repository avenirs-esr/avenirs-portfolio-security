package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.AuthContextDTO;
import fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper.AuthContextMapper;
import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.AuthenticationSessionReader;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/auth")
public class InternalAuthenticationController {

  private final AuthenticationService authenticationService;
  private final AuthenticationSessionReader authenticationSessionReader;

  public InternalAuthenticationController(
      AuthenticationService authenticationService,
      AuthenticationSessionReader authenticationSessionReader) {
    this.authenticationService = authenticationService;
    this.authenticationSessionReader = authenticationSessionReader;
  }

  @GetMapping("/context")
  public AuthContextDTO context(HttpServletRequest request) {
    try {
      OIDCSession oidcSession =
          authenticationSessionReader
              .readOidcSession(request)
              .orElseThrow(UnauthenticatedSessionException::new);

      OIDCSession refreshedSession = authenticationService.refreshSessionIfNeeded(oidcSession);

      if (!refreshedSession.equals(oidcSession)) {
        request.getSession(false).setAttribute(SessionAttributes.OIDC_SESSION, refreshedSession);
      }

      return AuthContextMapper.toDTO(
          authenticationService.getAuthenticatedContext(refreshedSession));

    } catch (UnauthenticatedSessionException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }
}
