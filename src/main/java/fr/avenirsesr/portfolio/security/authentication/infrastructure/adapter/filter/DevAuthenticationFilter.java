package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AllArgsConstructor
public class DevAuthenticationFilter extends OncePerRequestFilter {

  private PrincipalRepository principalRepository;
  private PrincipalGrantedAuthoritiesService principalGrantedAuthoritiesService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String eppn = request.getHeader("eppn");
    if (StringUtils.hasText(eppn)) {
      String login = principalRepository.findByEppn(eppn).map(Principal::getLogin).orElse(eppn);
      log.warn("Dev authentication enabled for user: {}, do not use in production", login);
      SecurityContextHolder.getContext()
          .setAuthentication(
              new UsernamePasswordAuthenticationToken(
                  login, "", principalGrantedAuthoritiesService.loadAuthorities(login)));
    }
    filterChain.doFilter(request, response);
  }
}
