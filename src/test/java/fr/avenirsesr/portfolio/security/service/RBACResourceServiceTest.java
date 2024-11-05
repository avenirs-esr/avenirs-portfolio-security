package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.model.RBACResource;
import fr.avenirsesr.portfolio.security.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.repository.RBACResourceSpecificationHelper;
import fr.avenirsesr.portfolio.security.repository.RBACResourceTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACResourceServiceTest {

    @Value("${avenirs.test.rbac.resource.service.resource.id}")
    private UUID resourceId;

    @Value("${avenirs.test.rbac.resource.service.resource.selector}")
    private String resourceSelector;

    @Value("${avenirs.test.rbac.resource.service.all.resource.selectors}")
    private String[] allResourceSelectors;

    @Value("${avenirs.test.rbac.resource.service.new.resource.selector}")
    private String newResourceSelector;

    @Value("${avenirs.test.rbac.resource.service.new.resource.type.id}")
    private UUID newResourceTypeId;


    @Value("${avenirs.test.rbac.resource.service.filtered.resource.ids}")
    private UUID[] filteredResourceIds;


    @Autowired
    private RBACResourceTypeRepository resourceTypeRepository;

    @Autowired
    private RBACResourceService resourceService;

    @Test
    void getResourceById() {

        RBACResource resource = resourceService.getResourceById(resourceId)
                .orElseThrow(() -> new AssertionError("Resource not found with ID: " + resourceId));

        assertEquals(resourceSelector, resource.getSelector(), "resource selector");

        Optional<RBACResource> response = resourceService.getResourceById(UUID.fromString("00000000-0000-0000-0000-000000000100"));
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllResources() {
        List<RBACResource> actual = resourceService.getAllResources();
        assertThat(actual).hasSize(allResourceSelectors.length);
        assertThat(actual.stream().map(RBACResource::getSelector)).containsExactlyInAnyOrder(allResourceSelectors);
    }

    @Test
    void getAllResourcesBySpecification() {
        List<RBACResource> actual = resourceService.getAllResourcesBySpecification(RBACResourceSpecificationHelper.filterByIds(filteredResourceIds));
        assertThat(actual).hasSize(filteredResourceIds.length);
        assertThat(actual.stream().map(RBACResource::getId)).containsExactlyInAnyOrder(filteredResourceIds);
    }

    @Test
    void createResource() {

        RBACResourceType resourceType = resourceTypeRepository.findById(newResourceTypeId)
                .orElseThrow(() -> new AssertionError("ResourceType not found with ID: " + newResourceTypeId));


        RBACResource newResource = new RBACResource()
                .setSelector(newResourceSelector)
                .setResourceType(resourceType);

        RBACResource savedResource = resourceService.createResource(newResource);
        assertNotNull(savedResource);

        RBACResource fetchedResource = resourceService.getResourceById(savedResource.getId())
                .orElseThrow(() -> new AssertionError("Resource not found with ID: " + savedResource.getId()));

        assertEquals(newResourceSelector, fetchedResource.getSelector(), "new resource selector");
        assertEquals(newResourceTypeId, fetchedResource.getResourceType().getId(), "new resource type id");

    }

    @Test
    void updateResource() {
        RBACResourceType resourceType = resourceTypeRepository.findById(newResourceTypeId)
                .orElseThrow(() -> new AssertionError("ResourceType not found with ID: " + newResourceTypeId));


        RBACResource newResource = new RBACResource()
                .setSelector(newResourceSelector)
                .setResourceType(resourceType);

        RBACResource savedResource = resourceService.createResource(newResource);
        assertNotNull(savedResource);

        assertEquals(newResourceSelector, savedResource.getSelector(), "Update Resource initial selector");
        String updatedSelector = newResourceSelector + "Updated";

        RBACResource updateResource = new RBACResource()
                .setId(newResource.getId())
                .setSelector(updatedSelector)
                .setResourceType(newResource.getResourceType());

        resourceService.updateResource(updateResource);

        RBACResource fetchedResource = resourceService.getResourceById(savedResource.getId())
                .orElseThrow(() -> new AssertionError("Resource not found with ID: " + savedResource.getId()));
        assertEquals(updatedSelector, fetchedResource.getSelector(), "Updated resource selector");
    }

    @Test
    void deleteResource() {

        RBACResourceType resourceType = resourceTypeRepository.findById(newResourceTypeId)
                .orElseThrow(() -> new AssertionError("ResourceType not found with ID: " + newResourceTypeId));


        RBACResource newResource = new RBACResource()
                .setSelector(newResourceSelector)
                .setResourceType(resourceType);

        RBACResource savedResource = resourceService.createResource(newResource);
        assertNotNull(savedResource);

        Optional<RBACResource> fetchedResource = resourceService.getResourceById(savedResource.getId());
        assertTrue(fetchedResource.isPresent(), "Delete Resource, present before delete");

        resourceService.deleteResource(savedResource.getId());

        fetchedResource = resourceService.getResourceById(savedResource.getId());
        assertFalse(fetchedResource.isPresent(), "Delete resource, resource deleted");
    }


}