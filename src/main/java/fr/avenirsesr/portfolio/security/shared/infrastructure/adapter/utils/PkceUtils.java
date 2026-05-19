package fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.utils;

import fr.avenirsesr.portfolio.security.authentication.domain.model.PkceChallenge;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public final class PkceUtils {
  private PkceUtils() {}

  public static PkceChallenge generate() {
    String verifier = UUID.randomUUID() + "-" + UUID.randomUUID();

    byte[] digest;
    try {
      digest =
          MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }

    String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);

    return new PkceChallenge(verifier, challenge);
  }
}
