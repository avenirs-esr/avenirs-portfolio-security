package fr.avenirsesr.portfolio.security.principal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.principal.domain.exception.StructureNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StructureServiceImplTest {

  private static final UUID STRUCTURE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_STRUCTURE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final String STRUCTURE_NAME = "RECIA";
  private static final String STRUCTURE_DESCRIPTION =
      "Groupement d'Intérêt Public Région Centre Interactive";
  private static final String UPDATED_STRUCTURE_NAME = "RECIA Updated";
  private static final String UPDATED_STRUCTURE_DESCRIPTION = "Updated description";
  private static final String UNKNOWN_STRUCTURE_NAME = "UNKNOWN";

  @Mock private StructureRepository structureRepository;

  private StructureServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new StructureServiceImpl(structureRepository);
  }

  @Test
  void getStructureByIdReturnsStructureWhenFound() {
    Structure structure = structure();

    when(structureRepository.findById(STRUCTURE_ID)).thenReturn(Optional.of(structure));

    Optional<Structure> result = service.getStructureById(STRUCTURE_ID);

    assertTrue(result.isPresent());
    assertEquals(structure, result.orElseThrow());

    verify(structureRepository).findById(STRUCTURE_ID);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void getStructureByIdReturnsEmptyWhenNotFound() {
    when(structureRepository.findById(UNKNOWN_STRUCTURE_ID)).thenReturn(Optional.empty());

    Optional<Structure> result = service.getStructureById(UNKNOWN_STRUCTURE_ID);

    assertTrue(result.isEmpty());

    verify(structureRepository).findById(UNKNOWN_STRUCTURE_ID);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void getStructureByNameReturnsStructureWhenFound() {
    Structure structure = structure();

    when(structureRepository.findByName(STRUCTURE_NAME)).thenReturn(Optional.of(structure));

    Optional<Structure> result = service.getStructureByName(STRUCTURE_NAME);

    assertTrue(result.isPresent());
    assertEquals(structure, result.orElseThrow());

    verify(structureRepository).findByName(STRUCTURE_NAME);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void getStructureByNameReturnsEmptyWhenNotFound() {
    when(structureRepository.findByName(UNKNOWN_STRUCTURE_NAME)).thenReturn(Optional.empty());

    Optional<Structure> result = service.getStructureByName(UNKNOWN_STRUCTURE_NAME);

    assertTrue(result.isEmpty());

    verify(structureRepository).findByName(UNKNOWN_STRUCTURE_NAME);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void getAllStructuresReturnsRepositoryStructures() {
    Structure structure = structure();

    when(structureRepository.findAll()).thenReturn(List.of(structure));

    List<Structure> result = service.getAllStructures();

    assertThat(result).containsExactly(structure);

    verify(structureRepository).findAll();
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void getAllStructuresReturnsEmptyListWhenRepositoryIsEmpty() {
    when(structureRepository.findAll()).thenReturn(List.of());

    List<Structure> result = service.getAllStructures();

    assertThat(result).isEmpty();

    verify(structureRepository).findAll();
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void createStructureSavesStructure() {
    Structure structureToCreate = new Structure(null, STRUCTURE_NAME, STRUCTURE_DESCRIPTION);
    Structure savedStructure = structure();

    when(structureRepository.save(structureToCreate)).thenReturn(savedStructure);

    Structure result = service.createStructure(structureToCreate);

    assertEquals(savedStructure, result);

    verify(structureRepository).save(structureToCreate);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void updateStructureSavesUpdatedStructureWhenExistingStructureIsFound() {
    Structure storedStructure = structure();
    Structure updateRequest =
        new Structure(STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);
    Structure expectedSavedStructure =
        new Structure(STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);

    when(structureRepository.findById(STRUCTURE_ID)).thenReturn(Optional.of(storedStructure));
    when(structureRepository.save(expectedSavedStructure)).thenReturn(expectedSavedStructure);

    Structure result = service.updateStructure(updateRequest);

    assertEquals(expectedSavedStructure, result);

    verify(structureRepository).findById(STRUCTURE_ID);
    verify(structureRepository).save(expectedSavedStructure);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void updateStructureThrowsWhenStructureDoesNotExist() {
    Structure updateRequest =
        new Structure(UNKNOWN_STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);

    when(structureRepository.findById(UNKNOWN_STRUCTURE_ID)).thenReturn(Optional.empty());

    StructureNotFoundException exception =
        assertThrows(
            StructureNotFoundException.class, () -> service.updateStructure(updateRequest));

    assertEquals("No structure found for id : " + UNKNOWN_STRUCTURE_ID, exception.getMessage());

    verify(structureRepository).findById(UNKNOWN_STRUCTURE_ID);
    verifyNoMoreInteractions(structureRepository);
  }

  @Test
  void deleteStructureDeletesById() {
    service.deleteStructure(STRUCTURE_ID);

    verify(structureRepository).deleteById(STRUCTURE_ID);
    verifyNoMoreInteractions(structureRepository);
  }

  private Structure structure() {
    return new Structure(STRUCTURE_ID, STRUCTURE_NAME, STRUCTURE_DESCRIPTION);
  }
}
