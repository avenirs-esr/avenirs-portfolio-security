package fr.avenirsesr.portfolio.security.principal.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureRepository {

  Optional<Structure> findById(UUID structureId);

  Optional<Structure> findByName(String structureName);

  List<Structure> findAll();

  Structure save(Structure structure);

  void deleteById(UUID structureId);

  List<Structure> findAllByIds(List<UUID> ids);
}
