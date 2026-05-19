package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.utils.RedirectUtils.toSafeHost;
import static fr.avenirsesr.portfolio.common.utils.RedirectUtils.toSafeRelativePath;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  @Value("${avenirs.authentication.oidc.logout.url}")
  private String casLogoutUrl;

  @Value("${avenirs.authentication.auth.logout.default-service}")
  private String logoutDefaultService;

  @Value("${server.servlet.session.cookie.name}")
  private String sessionCookieName;

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
        authenticationService.generateAuthorizationUrl(toSafeHost(host), safeRedirect);

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
        authenticationService.createSessionFromAuthorizationCode(toSafeHost(host), code);

    session.setAttribute(SessionAttributes.OIDC_SESSION, oidcSession);

    String safeRedirect = toSafeRelativePath(state, "/cofolio/student");
    response.sendRedirect("https://" + (toSafeHost(host)) + safeRedirect);
  }

  @GetMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);

    if (session != null) {
      session.invalidate();
    }

    ResponseCookie sessionCookie =
        ResponseCookie.from(sessionCookieName, "")
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .maxAge(0)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());

    String logoutRedirectUrl =
        UriComponentsBuilder.fromUriString(casLogoutUrl)
            .queryParam("service", logoutDefaultService)
            .build()
            .encode()
            .toUriString();

    response.sendRedirect(logoutRedirectUrl);
  }
}
