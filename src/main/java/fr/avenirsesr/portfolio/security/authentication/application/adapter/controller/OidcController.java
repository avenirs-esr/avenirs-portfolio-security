package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.IntrospectResponseDTO;
import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.LoginRequestDTO;
import fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper.IntrospectResponseMapper;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper.OIDCAccessTokenMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper.OIDCProfileMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCProfileResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication Controller. Interact with an OIDC provider, Apereo CAS for instance. Can
 * retrieve/Validate/introspect an access token.
 */
@Slf4j
@RestController
public class OidcController {
  /** Constant for a missing authorization code. */
  public static final String NO_PROVIDED_CODE = "NO_PROVIDED_CODE";

  /** Authentication service. */
  private final OidcService oidcService;

  public OidcController(OidcService oidcService) {
    this.oidcService = oidcService;
  }

  /**
   * Gives an access token.
   *
   * @param request The object with the credentials.
   * @return The access token.
   * @throws IOException If the access token could not be retrieved.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) throws IOException {
    log.trace("login, request: {}", request);
    OIDCAccessToken accessToken =
        oidcService
            .getAccessToken(request.getLogin(), request.getPassword())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid login credentials"));
    return ResponseEntity.ok(accessToken.accessToken());
  }

  /**
   * Callback after OIDC authentication.
   *
   * @param host The header used to retrieve the current host. This is used to determine the end
   *     point from the current request.
   * @param code The session code used to issue an access token.
   * @param codeVerifier The PKCE code verifier used to issue an access token.
   * @throws IOException If an input or output exception occurs.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @GetMapping("${avenirs.authentication.oidc.callback}")
  public ResponseEntity<OIDCAccessTokenResponse> oidcCallback(
      @RequestHeader(value = "x-forwarded-host", required = false) String host,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "code_verifier", required = false) String codeVerifier)
      throws IOException {
    log.trace("oidcCallback");

    if (code == null || code.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization code");
    }

    if (codeVerifier == null || codeVerifier.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing PKCE code verifier");
    }

    OIDCAccessToken accessToken =
        this.oidcService.exchangeAuthorizationCodeForToken(host, code, codeVerifier);

    return ResponseEntity.ok(OIDCAccessTokenMapper.fromDomain(accessToken));
  }

  /**
   * Performs the redirection after the access token is retrieved.
   *
   * @param host The header used to determine the host.
   * @param response The servlet response instance, used to perform a redirection.
   * @throws IOException If an input or output exception occurs.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @GetMapping("${avenirs.authentication.oidc.callback.redirect}")
  public void redirect(
      @RequestHeader(value = "x-forwarded-host", required = false) String host,
      HttpServletResponse response)
      throws IOException {
    log.trace("redirect");
    response.sendRedirect(this.oidcService.generateServiceURL(host == null ? "localhost" : host));
  }

  /**
   * Access token introspection end point.
   *
   * @param token The token to introspect.
   * @return The response of the OIDC provider.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @PostMapping("${avenirs.authentication.oidc.callback.profile}")
  public OIDCProfileResponse profile(@RequestHeader(value = "x-authorization") String token) {
    OIDCIntrospection introspection = this.oidcService.introspectAccessToken(token);

    if (introspection != null && introspection.active()) {
      OIDCProfile profile = this.oidcService.profile(token);
      return OIDCProfileMapper.fromDomain(profile);
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  }

  /**
   * Access token introspection end point.
   *
   * @param token The token to introspect.
   * @return The OIDC Provider response.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @PostMapping("${avenirs.authentication.oidc.callback.introspect}")
  public IntrospectResponseDTO introspect(@RequestHeader(value = "x-authorization") String token) {
    return IntrospectResponseMapper.toDTO(this.oidcService.introspectAccessToken(token));
  }
}
