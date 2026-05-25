package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.client.ExternalUserClient;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository.PrincipalJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrincipalSeeder {

  @Value("${seeder.principal.default-password}")
  private String defaultPassword;

  private final ExternalUserClient externalUserClient;
  private final PrincipalJpaRepository principalJpaRepository;

  @Transactional
  public List<PrincipalEntity> seed() {
    List<ExternalUserDTO> externalUsers = externalUserClient.getAll();

    List<PrincipalEntity> principalEntities =
        externalUsers.stream()
            .filter(ExternalUserDTO::isActive)
            .map(this::toPrincipalEntity)
            .toList();

    List<PrincipalEntity> savedPrincipalEntities =
        principalJpaRepository.saveAll(principalEntities);

    log.info(
        "✔ {} principals created from interoperability external users",
        savedPrincipalEntities.size());

    return savedPrincipalEntities;
  }

  private PrincipalEntity toPrincipalEntity(ExternalUserDTO externalUser) {
    Instant now = Instant.now();

    return PrincipalEntity.of(
        UUID.randomUUID(),
        externalUser.eppn(),
        externalUser.eppn(),
        defaultPassword,
        externalUser.source(),
        externalUser.externalId(),
        externalUser.category(),
        EUserStatus.ACTIVE,
        now,
        now);
  }
}
