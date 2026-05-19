package fr.avenirsesr.portfolio.security.principal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.principal.domain.exception.StructureNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GivenStructureService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a structure service");
    }

    @Nested
    class WhenGettingStructureById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting structure by id");
      }

      @Nested
      class AndTheStructureExists {
        private Structure structure;
        private Optional<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure exists");

          structure = structure();

          when(structureRepository.findById(STRUCTURE_ID)).thenReturn(Optional.of(structure));

          result = service.getStructureById(STRUCTURE_ID);
        }

        @Test
        void thenItShouldReturnStructure() {
          BddLogger.then("it should return structure");

          assertTrue(result.isPresent());
          assertEquals(structure, result.orElseThrow());

          verify(structureRepository).findById(STRUCTURE_ID);
          verifyNoMoreInteractions(structureRepository);
        }
      }

      @Nested
      class AndTheStructureDoesNotExist {
        private Optional<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure does not exist");

          when(structureRepository.findById(UNKNOWN_STRUCTURE_ID)).thenReturn(Optional.empty());

          result = service.getStructureById(UNKNOWN_STRUCTURE_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(structureRepository).findById(UNKNOWN_STRUCTURE_ID);
          verifyNoMoreInteractions(structureRepository);
        }
      }
    }

    @Nested
    class WhenGettingStructureByName {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting structure by name");
      }

      @Nested
      class AndTheStructureExists {
        private Structure structure;
        private Optional<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure exists");

          structure = structure();

          when(structureRepository.findByName(STRUCTURE_NAME)).thenReturn(Optional.of(structure));

          result = service.getStructureByName(STRUCTURE_NAME);
        }

        @Test
        void thenItShouldReturnStructure() {
          BddLogger.then("it should return structure");

          assertTrue(result.isPresent());
          assertEquals(structure, result.orElseThrow());

          verify(structureRepository).findByName(STRUCTURE_NAME);
          verifyNoMoreInteractions(structureRepository);
        }
      }

      @Nested
      class AndTheStructureDoesNotExist {
        private Optional<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure does not exist");

          when(structureRepository.findByName(UNKNOWN_STRUCTURE_NAME)).thenReturn(Optional.empty());

          result = service.getStructureByName(UNKNOWN_STRUCTURE_NAME);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(structureRepository).findByName(UNKNOWN_STRUCTURE_NAME);
          verifyNoMoreInteractions(structureRepository);
        }
      }
    }

    @Nested
    class WhenGettingAllStructures {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all structures");
      }

      @Nested
      class AndTheRepositoryContainsStructures {
        private Structure structure;
        private List<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains structures");

          structure = structure();

          when(structureRepository.findAll()).thenReturn(List.of(structure));

          result = service.getAllStructures();
        }

        @Test
        void thenItShouldReturnRepositoryStructures() {
          BddLogger.then("it should return repository structures");

          assertThat(result).containsExactly(structure);

          verify(structureRepository).findAll();
          verifyNoMoreInteractions(structureRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<Structure> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(structureRepository.findAll()).thenReturn(List.of());

          result = service.getAllStructures();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(structureRepository).findAll();
          verifyNoMoreInteractions(structureRepository);
        }
      }
    }

    @Nested
    class WhenCreatingStructure {
      private Structure structureToCreate;
      private Structure savedStructure;
      private Structure result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating structure");

        structureToCreate = new Structure(null, STRUCTURE_NAME, STRUCTURE_DESCRIPTION);
        savedStructure = structure();

        when(structureRepository.save(structureToCreate)).thenReturn(savedStructure);

        result = service.createStructure(structureToCreate);
      }

      @Test
      void thenItShouldSaveStructure() {
        BddLogger.then("it should save structure");

        assertEquals(savedStructure, result);

        verify(structureRepository).save(structureToCreate);
        verifyNoMoreInteractions(structureRepository);
      }
    }

    @Nested
    class WhenUpdatingStructure {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating structure");
      }

      @Nested
      class AndTheStructureExists {
        private Structure expectedSavedStructure;
        private Structure result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure exists");

          Structure storedStructure = structure();
          Structure updateRequest =
              new Structure(STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);

          expectedSavedStructure =
              new Structure(STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);

          when(structureRepository.findById(STRUCTURE_ID)).thenReturn(Optional.of(storedStructure));
          when(structureRepository.save(expectedSavedStructure)).thenReturn(expectedSavedStructure);

          result = service.updateStructure(updateRequest);
        }

        @Test
        void thenItShouldSaveUpdatedStructure() {
          BddLogger.then("it should save updated structure");

          assertEquals(expectedSavedStructure, result);

          verify(structureRepository).findById(STRUCTURE_ID);
          verify(structureRepository).save(expectedSavedStructure);
          verifyNoMoreInteractions(structureRepository);
        }
      }

      @Nested
      class AndTheStructureDoesNotExist {
        private Structure updateRequest;
        private StructureNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the structure does not exist");

          updateRequest =
              new Structure(
                  UNKNOWN_STRUCTURE_ID, UPDATED_STRUCTURE_NAME, UPDATED_STRUCTURE_DESCRIPTION);

          when(structureRepository.findById(UNKNOWN_STRUCTURE_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(
                  StructureNotFoundException.class, () -> service.updateStructure(updateRequest));
        }

        @Test
        void thenItShouldThrowStructureNotFoundException() {
          BddLogger.then("it should throw structure not found exception");

          assertEquals(
              "No structure found for id : " + UNKNOWN_STRUCTURE_ID, exception.getMessage());

          verify(structureRepository).findById(UNKNOWN_STRUCTURE_ID);
          verifyNoMoreInteractions(structureRepository);
        }
      }
    }

    @Nested
    class WhenDeletingStructure {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting structure");

        service.deleteStructure(STRUCTURE_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(structureRepository).deleteById(STRUCTURE_ID);
        verifyNoMoreInteractions(structureRepository);
      }
    }
  }

  private Structure structure() {
    return new Structure(STRUCTURE_ID, STRUCTURE_NAME, STRUCTURE_DESCRIPTION);
  }
}
