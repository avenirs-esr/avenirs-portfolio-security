package fr.avenirsesr.portfolio.security.principal.domain.service;

import fr.avenirsesr.portfolio.security.principal.domain.exception.StructureNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.StructureService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class StructureServiceImpl implements StructureService {

  private final StructureRepository structureRepository;

  @Override
  public Optional<Structure> getStructureById(UUID structureId) {
    log.trace("getStructureById structureId: {}", structureId);
    return structureRepository.findById(structureId);
  }

  @Override
  public Optional<Structure> getStructureByName(String structureName) {
    log.trace("getStructureByName structureName: {}", structureName);
    return structureRepository.findByName(structureName);
  }

  @Override
  public List<Structure> getAllStructures() {
    log.trace("getAllStructures");
    return structureRepository.findAll();
  }

  @Override
  public Structure createStructure(Structure structure) {
    log.trace("createStructure, structure: {}", structure);
    return structureRepository.save(structure);
  }

  @Override
  public Structure updateStructure(Structure structure) {
    log.trace("updateStructure, structure: {}", structure);

    return structureRepository
        .findById(structure.id())
        .map(
            storedStructure ->
                new Structure(storedStructure.id(), structure.name(), structure.description()))
        .map(structureRepository::save)
        .orElseThrow(
            () ->
                new StructureNotFoundException(
                    "No structure found for id : " + structure.id().toString()));
  }

  @Override
  public void deleteStructure(UUID structureId) {
    log.trace("deleteStructure, structureId: {}", structureId);
    structureRepository.deleteById(structureId);
  }
}
