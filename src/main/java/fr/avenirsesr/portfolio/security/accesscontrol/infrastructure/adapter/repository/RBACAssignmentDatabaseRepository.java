package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACAssignmentMapper;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACContextMapper;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.specification.RBACAssignmentSpecificationHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACAssignmentDatabaseRepository implements RBACAssignmentRepository {

  private final RBACAssignmentJpaRepository rbacAssignmentJpaRepository;
  private final RBACAssignmentMapper assignmentMapper;
  private final RBACContextMapper contextMapper;

  @Override
  @Transactional
  public RBACAssignment save(RBACAssignment assignment) {
    var entity = assignmentMapper.fromDomain(assignment);
    var savedEntity = rbacAssignmentJpaRepository.save(entity);
    return assignmentMapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public void deleteById(UUID assignmentId) {
    rbacAssignmentJpaRepository.deleteById(assignmentId);
  }

  @Override
  @Transactional
  public List<RBACAssignment> findByPrincipalContextAndResource(
      String login, RBACContext context, UUID resourceId) {
    var contextEntity = contextMapper.fromDomain(context);

    return rbacAssignmentJpaRepository
        .findAll(
            RBACAssignmentSpecificationHelper.filterByPrincipalContextAndResources(
                login, contextEntity, resourceId))
        .stream()
        .map(assignmentMapper::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public List<RBACAssignment> findAll() {
    return rbacAssignmentJpaRepository.findAll().stream().map(assignmentMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public Optional<RBACAssignment> findById(UUID assignmentId) {
    return rbacAssignmentJpaRepository.findById(assignmentId).map(assignmentMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACAssignment> findByPrincipal(String login) {
    return rbacAssignmentJpaRepository
        .findAll(RBACAssignmentSpecificationHelper.filterByPrincipal(login))
        .stream()
        .map(assignmentMapper::toDomain)
        .toList();
  }
}
