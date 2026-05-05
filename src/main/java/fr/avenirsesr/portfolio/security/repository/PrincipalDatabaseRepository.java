package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.authentication.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.model.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrincipalDatabaseRepository implements PrincipalRepository {

  private final PrincipalJpaRepository principalJpaRepository;

  @Override
  public Optional<Principal> findByProviderAndExternalId(String provider, String externalId) {
    return principalJpaRepository.findByProviderAndExternalId(provider, externalId);
  }
}
