package fr.avenirsesr.portfolio.security.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.security.model.Principal;
import fr.avenirsesr.portfolio.security.repository.PrincipalJpaRepository;
import fr.avenirsesr.portfolio.security.seeder.data.PrincipalCreationData;
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
  public List<Principal> seed() {
    List<PrincipalCreationData> creationData =
        fileReader.readJSON(PATH_FILE, new TypeReference<>() {});

    List<Principal> principals =
        creationData.stream()
            .map(
                data ->
                    Principal.of(
                        null, data.login(), data.provider(), data.externalId(), data.userId()))
            .toList();

    List<Principal> savedPrincipals = principalJpaRepository.saveAll(principals);

    log.info("✔ {} principals created", savedPrincipals.size());

    return savedPrincipals;
  }
}
