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

  @Value("${seeder.principal.amu.default-password}")
  private String amuDefaultPassword;

  @Value("${seeder.principal.ubo.default-password}")
  private String uboDefaultPassword;

  @Value("${seeder.principal.usmb.default-password}")
  private String usmbDefaultPassword;

  @Value("${seeder.principal.ulille.default-password}")
  private String ulilleDefaultPassword;

  @Value("${seeder.principal.rpltk.default-password}")
  private String rpltkDefaultPassword;

  @Value("${seeder.principal.utln.default-password}")
  private String utlnDefaultPassword;

  @Value("${seeder.principal.nu.default-password}")
  private String nuDefaultPassword;

  private final ExternalUserClient externalUserClient;
  private final PrincipalJpaRepository principalJpaRepository;

  @Transactional
  public List<PrincipalEntity> seed() {
    List<ExternalUserDTO> externalUsers = externalUserClient.getAll();

    List<PrincipalEntity> principalEntities =
        externalUsers.stream().map(this::toPrincipalEntity).toList();

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
        resolvePassword(externalUser.eppn()),
        externalUser.source(),
        externalUser.externalId(),
        externalUser.categories(),
        EUserStatus.ACTIVE,
        now,
        now);
  }

  private String resolvePassword(String eppn) {
    if (eppn == null || eppn.isBlank()) {
      return defaultPassword;
    }

    String normalizedEppn = eppn.toUpperCase();

    if (normalizedEppn.contains("ULILLE")) {
      return ulilleDefaultPassword;
    }
    if (normalizedEppn.contains("RPLTK")) {
      return rpltkDefaultPassword;
    }
    if (normalizedEppn.contains("USMB")) {
      return usmbDefaultPassword;
    }
    if (normalizedEppn.contains("UTLN")) {
      return utlnDefaultPassword;
    }
    if (normalizedEppn.contains("AMU")) {
      return amuDefaultPassword;
    }
    if (normalizedEppn.contains("UBO")) {
      return uboDefaultPassword;
    }
    if (normalizedEppn.contains("NU")) {
      return nuDefaultPassword;
    }

    return defaultPassword;
  }
}
