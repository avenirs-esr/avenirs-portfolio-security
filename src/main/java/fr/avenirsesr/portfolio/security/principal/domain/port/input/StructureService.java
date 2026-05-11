package fr.avenirsesr.portfolio.security.principal.domain.port.input;

import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureService {

  Optional<Structure> getStructureById(UUID structureId);

  Optional<Structure> getStructureByName(String structureName);

  List<Structure> getAllStructures();

  Structure createStructure(Structure structure);

  Structure updateStructure(Structure structure);

  void deleteStructure(UUID structureId);
}
