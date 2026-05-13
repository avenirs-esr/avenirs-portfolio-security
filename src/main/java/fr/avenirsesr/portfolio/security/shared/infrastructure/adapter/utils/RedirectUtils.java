package fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.utils;

public final class RedirectUtils {
  private static final String DEFAULT_REDIRECT_PATH = "/cofolio/student";

  private RedirectUtils() {}

  public static String toSafeRelativePath(String value) {
    return toSafeRelativePath(value, DEFAULT_REDIRECT_PATH);
  }

  public static String toSafeRelativePath(String value, String fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }

    if (!value.startsWith("/")) {
      return fallback;
    }

    if (value.startsWith("//")) {
      return fallback;
    }

    return value;
  }
}
