package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.mapper.StructureMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StructureDatabaseRepository implements StructureRepository {

  private final StructureJpaRepository structureJpaRepository;
  private final StructureMapper structureMapper;

  @Override
  @Transactional(readOnly = true)
  public Optional<Structure> findById(UUID structureId) {
    return structureJpaRepository.findById(structureId).map(structureMapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Structure> findByName(String structureName) {
    return structureJpaRepository.findByName(structureName).map(structureMapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Structure> findAll() {
    return structureJpaRepository.findAll().stream().map(structureMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public Structure save(Structure structure) {
    return structureMapper.toDomain(
        structureJpaRepository.save(structureMapper.fromDomain(structure)));
  }

  @Override
  @Transactional
  public void deleteById(UUID structureId) {
    structureJpaRepository.deleteById(structureId);
  }

  @Override
  @Transactional
  public List<Structure> findAllByIds(List<UUID> ids) {
    return structureJpaRepository.findAllById(ids).stream().map(structureMapper::toDomain).toList();
  }
}
