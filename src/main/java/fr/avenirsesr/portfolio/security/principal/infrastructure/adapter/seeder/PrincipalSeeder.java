package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository.PrincipalJpaRepository;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder.data.PrincipalCreationData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrincipalSeeder {

  private static final String PATH_FILE = "seeder/principals.json";

  private final FileReader fileReader;
  private final PrincipalJpaRepository principalJpaRepository;

  @Transactional
  public List<PrincipalEntity> seed() {
    List<PrincipalCreationData> creationData =
        fileReader.readJSON(PATH_FILE, new TypeReference<>() {});

    List<PrincipalEntity> principalEntities =
        creationData.stream()
            .map(
                data ->
                    PrincipalEntity.of(
                        null, data.login(), data.provider(), data.externalId(), data.userId()))
            .toList();

    List<PrincipalEntity> savedPrincipalEntities =
        principalJpaRepository.saveAll(principalEntities);

    log.info("✔ {} principals created", savedPrincipalEntities.size());

    return savedPrincipalEntities;
  }
}
