package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.model.Structure;
import fr.avenirsesr.portfolio.security.repositories.StructureSpecificationHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class StructureServiceTest {

    @Value("${avenirs.test.rbac.structure.service.structure.id}")
    private Long structureId;

    @Value("${avenirs.test.rbac.structure.service.structure.name}")
    private String structureName;

    @Value("${avenirs.test.rbac.structure.service.structure.description}")
    private String structureDescription;

    @Value("${avenirs.test.rbac.structure.service.new.structure.name}")
    private String newStructureName;

    @Value("${avenirs.test.rbac.structure.service.new.structure.description}")
    private String newStructureDescription;

    @Value("${avenirs.test.rbac.structure.service.all.structure.names}")
    private String[] allStructureNames;

    @Value("${avenirs.test.rbac.structure.service.filtered.structure.ids}")
    private Long[] filteredStructureIds;

    @Autowired
    private StructureService structureService;

    @Test
    void getStructureById() {

        Structure structure = structureService.getStructureById(structureId)
                .orElseThrow(() -> new AssertionError("Structure not found with ID: " + structureId));

        assertEquals(structureName, structure.getName());
        assertEquals(structureDescription, structure.getDescription());

        Optional<Structure> response = structureService.getStructureById((long) allStructureNames.length + 1);
        assertTrue(response.isEmpty());
    }

    @Test
    void getStructureByName() {

        Structure structure = structureService.getStructureByName(structureName)
                .orElseThrow(() -> new AssertionError("Structure not found with Name: " + structureName));

        assertEquals(structureId, structure.getId());
        assertEquals(structureName, structure.getName());
        assertEquals(structureDescription, structure.getDescription());

        Optional<Structure> response = structureService.getStructureByName(newStructureName);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllStructures() {
        List<Structure> actual = structureService.getAllStructures();
        assertThat(actual).hasSize(allStructureNames.length);
        assertThat(actual.stream().map(Structure::getName)).containsExactlyInAnyOrder(allStructureNames);
    }

    @Test
    void getAllStructuresBySpecification() {
        List<Structure> actual = structureService.getAllStructuresBySpecification(StructureSpecificationHelper.filterByIds(filteredStructureIds));
        assertThat(actual).hasSize(filteredStructureIds.length);
        assertThat(actual.stream().map(Structure::getId)).containsExactlyInAnyOrder(filteredStructureIds);
    }


    @Test
    void createStructure() {
        Structure newStructure = new Structure()
                .setName(newStructureName)
                .setDescription(newStructureDescription);

        Structure savedStructure = structureService.createStructure(newStructure);
        assertNotNull(savedStructure);

        Structure fetchedStructure = structureService.getStructureById(savedStructure.getId())
                .orElseThrow(() -> new AssertionError("Structure not found with ID: " + savedStructure.getId()));

        assertEquals(newStructureName, fetchedStructure.getName(), "new Structure name");
        assertEquals(newStructureDescription, fetchedStructure.getDescription(), "new Structure description");
    }


    @Test
    void updateStructure() {
        Structure newStructure = new Structure()
                .setName(newStructureName)
                .setDescription(newStructureDescription);

        Structure savedStructure = structureService.createStructure(newStructure);
        assertNotNull(savedStructure);

        assertEquals(newStructureName, savedStructure.getName(), "Update Structure initial name");
        String updatedName = newStructureName + "Updated";

        Structure updateStructure = new Structure()
                .setId(newStructure.getId())
                .setName(updatedName)
                .setDescription(newStructureDescription);

        updateStructure = structureService.updateStructure(updateStructure);

        assertNotNull(updateStructure, "Updated structure not null");
        
        Structure fetchedStructure = structureService.getStructureById(savedStructure.getId())
                .orElseThrow(() -> new AssertionError("Structure not found with ID: " + savedStructure.getId()));
        assertEquals(updatedName, fetchedStructure.getName(), "Updated structure name");
    }

   @Test
    void deleteStructure() {

        Structure newStructure = new Structure()
                .setName(newStructureName)
                .setDescription(newStructureDescription);

        Structure savedStructure = structureService.createStructure(newStructure);

        assertNotNull(savedStructure);

        Optional<Structure> fetchedStructure = structureService.getStructureById(savedStructure.getId());
        assertTrue(fetchedStructure.isPresent(), "Delete Structure, present before delete");

        structureService.deleteStructure(savedStructure.getId());

        fetchedStructure = structureService.getStructureById(savedStructure.getId());
        assertFalse(fetchedStructure.isPresent(), "Delete structure, structure deleted");
    }
}