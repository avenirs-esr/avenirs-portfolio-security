package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCAccessTokenResponse;
import java.util.Map;

public final class OIDCAccessTokenMapper {

  private OIDCAccessTokenMapper() {}

  public static OIDCAccessToken toDomain(OIDCAccessTokenResponse payload) {
    if (payload == null) {
      return null;
    }

    return new OIDCAccessToken(
        payload.getAccessToken(),
        payload.getRefreshToken(),
        payload.getTokenType(),
        payload.getExpireIn(),
        payload.getScope(),
        payload.getIdToken(),
        payload.getClaims(),
        payload.isJwt());
  }

  public static OIDCAccessTokenResponse fromDomain(OIDCAccessToken domain) {
    if (domain == null) {
      return null;
    }

    OIDCAccessTokenResponse payload = new OIDCAccessTokenResponse();
    payload.setAccessToken(domain.accessToken());
    payload.setRefreshToken(domain.refreshToken());
    payload.setTokenType(domain.tokenType());
    payload.setExpireIn(domain.expiresIn());
    payload.setScope(domain.scope());
    payload.setClaims(domain.claims());
    payload.setJwt(domain.jwt());
    payload.setIdToken(domain.rawIdToken());

    return payload;
  }

  public static Map<String, Object> claimsFromDomain(OIDCAccessToken domain) {
    return domain != null ? domain.claims() : null;
  }
}
