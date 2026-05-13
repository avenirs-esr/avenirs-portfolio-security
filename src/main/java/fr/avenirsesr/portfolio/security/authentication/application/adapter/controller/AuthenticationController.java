package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.utils.RedirectUtils.toSafeRelativePath;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.*;

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
  public void callback() {}

  @GetMapping("/logout")
  public void logout() {}
}
