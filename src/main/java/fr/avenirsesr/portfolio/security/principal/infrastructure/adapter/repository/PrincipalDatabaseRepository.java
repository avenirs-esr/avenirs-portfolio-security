package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper.PrincipalMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PrincipalDatabaseRepository implements PrincipalRepository {

  private final PrincipalJpaRepository principalJpaRepository;
  private static final PrincipalMapper principalMapper = PrincipalMapper.INSTANCE;

  @Override
  @Transactional
  public List<Principal> findAll() {
    return principalJpaRepository.findAll().stream().map(principalMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public Optional<Principal> findByLogin(String login) {
    return principalJpaRepository.findByLogin(login).map(principalMapper::toDomain);
  }

  @Override
  @Transactional
  public Optional<Principal> findByProviderAndExternalId(String provider, String externalId) {
    return principalJpaRepository
        .findByProviderAndExternalId(provider, externalId)
        .map(principalMapper::toDomain);
  }

  @Override
  public Optional<Principal> findByEppn(String eppn) {
    return principalJpaRepository.findByEppn(eppn).map(principalMapper::toDomain);
  }
}
