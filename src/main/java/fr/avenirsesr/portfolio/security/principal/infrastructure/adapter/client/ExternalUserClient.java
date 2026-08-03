package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ExternalUserClient {

  private final WebClient webClient;

  @Value("${avenirs.back-office.api-key}")
  private String apiKey;

  @Value("${avenirs.back-office.external-user.endpoint}")
  private String externalUserEndpoint;

  public ExternalUserClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public List<ExternalUserDTO> getAll() {
    try {
      log.debug("Fetching external users from back office");

      List<ExternalUserDTO> result =
          webClient
              .get()
              .uri(externalUserEndpoint)
              .header(AvenirsSecurityHeaders.API_KEY, apiKey)
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<List<ExternalUserDTO>>() {})
              .block();

      return result != null ? result : List.of();
    } catch (Exception e) {
      log.error(
          "Failed to fetch external users from back office at '{}'. Error: {}",
          externalUserEndpoint,
          e.getMessage());
      log.debug("Full error details:", e);
      return List.of();
    }
  }
}
