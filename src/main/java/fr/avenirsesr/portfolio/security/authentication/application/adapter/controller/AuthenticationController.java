package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.utils.RedirectUtils.toSafeRelativePath;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @GetMapping("/login")
  public void login(
      @RequestHeader(value = "x-forwarded-host", required = false) String host,
      @RequestParam(value = "redirect", required = false) String redirect,
      HttpServletResponse response)
      throws IOException {
    String safeRedirect = toSafeRelativePath(redirect, "/cofolio/student");

    String authorizeUrl =
        authenticationService.generateAuthorizationUrl(
            host == null ? "localhost" : host, safeRedirect);

    response.sendRedirect(authorizeUrl);
  }

  @GetMapping("/callback")
  public void callback(
      @RequestHeader(value = "x-forwarded-host", required = false) String host,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state,
      HttpSession session,
      HttpServletResponse response)
      throws IOException {
    if (code == null || code.isBlank()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization code");
      return;
    }

    OIDCSession oidcSession =
        authenticationService.createSessionFromAuthorizationCode(
            host == null ? "localhost" : host, code);

    session.setAttribute(SessionAttributes.OIDC_SESSION, oidcSession);

    String safeRedirect = toSafeRelativePath(state, "/cofolio/student");
    response.sendRedirect("https://" + (host == null ? "localhost" : host) + safeRedirect);
  }

  @GetMapping("/logout")
  public void logout() {}
}
