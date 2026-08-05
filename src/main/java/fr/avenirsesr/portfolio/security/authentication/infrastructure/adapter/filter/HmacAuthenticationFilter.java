package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.UserSecurityPayload;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AllArgsConstructor
public class HmacAuthenticationFilter extends OncePerRequestFilter {

  private final String secret;
  private final PrincipalGrantedAuthoritiesService principalGrantedAuthoritiesService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String signature = request.getHeader(AvenirsSecurityHeaders.CONTEXT_SIGNATURE);
    String payload = request.getHeader(AvenirsSecurityHeaders.SIGNED_CONTEXT);

    if (StringUtils.hasText(signature) && StringUtils.hasText(payload)) {
      UserSecurityPayload securityPayload = readPayload(payload);

      if (isValid(securityPayload) && verifySignature(payload, signature)) {
        String sub = securityPayload.getSub();
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    sub, "", principalGrantedAuthoritiesService.loadAuthorities(sub)));
      } else {
        log.warn("Invalid or expired signed context: request will not be authenticated");
      }
    }

    filterChain.doFilter(request, response);
  }

  private UserSecurityPayload readPayload(String payload) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper.readValue(payload, UserSecurityPayload.class);
    } catch (IOException e) {
      return null;
    }
  }

  private boolean isValid(UserSecurityPayload payload) {
    return payload != null && payload.getExp() != null && payload.getExp().isAfter(Instant.now());
  }

  private boolean verifySignature(String payload, String signature) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      sha256Hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      String computedSignature =
          Base64.getEncoder()
              .encodeToString(sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
      return computedSignature.equals(signature);
    } catch (Exception e) {
      return false;
    }
  }
}
