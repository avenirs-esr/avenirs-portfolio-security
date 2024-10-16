package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.models.*;
import fr.avenirsesr.portfolio.security.repositories.RBACScopeSpecificationHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACScopeServiceTest {


    @Value("${avenirs.test.rbac.scope.service.scope.id}")
    private Long scopeId;

    @Value("${avenirs.test.rbac.scope.service.scope.name}")
    private String scopeName;
    @Value("${avenirs.test.rbac.scope.service.scope.resource.id}")
    private Long scopeResourceId;

    @Value("${avenirs.test.rbac.scope.service.new.scope.name}")
    private String newScopeName;

    @Value("${avenirs.test.rbac.scope.service.new.scope.resources.number}")
    private int newScopeResourcesNumber;

    @Value("${avenirs.test.rbac.scope.service.new.scope.resources.type}")
    private int newScopeResourcesType;

    @Value("${avenirs.test.rbac.scope.service.all.scope.names}")
    private String[] allScopeNames;

    @Value("${avenirs.test.rbac.scope.service.resources.filter}")
    private Long[] resourcesFilter;

    @Value("${avenirs.test.rbac.scope.service.filtered.scope.ids}")
    private Long[] filteredScopeIds;


    @Autowired
    private RBACScopeService scopeService;

    @Test
    void getScopeById() {
        Optional<RBACScope> response = scopeService.getScopeById(scopeId);
        assertTrue(response.isPresent());

        RBACScope scope = response.get();
        assertEquals(scopeName, scope.getName());

        assertEquals(1, scope.getResources().size());
        assertEquals(scopeResourceId, scope.getResources().getFirst().getId());

        response = scopeService.getScopeById((long)allScopeNames.length + 1);
        assertTrue(response.isEmpty());
    }

    @Test
    void getScopeByName() {
        Optional<RBACScope> response = scopeService.getScopeByName(scopeName);
        assertTrue(response.isPresent());

        RBACScope scope = response.get();
        assertEquals(scopeId, scope.getId());
        assertEquals(scopeName, scope.getName());


        assertEquals(1, scope.getResources().size());
        assertEquals(scopeResourceId, scope.getResources().getFirst().getId());

        response = scopeService.getScopeByName(newScopeName);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllScopes() {
        List<RBACScope> actual  = scopeService.getAllScopes();
        assertThat(actual).hasSize(allScopeNames.length);
        assertThat(actual.stream().map(RBACScope::getName)).containsExactlyInAnyOrder(allScopeNames);
    }

    @Test
    void getAllScopesBySpecification() {

        List<RBACScope> actual = scopeService.getAllScopesBySpecification(RBACScopeSpecificationHelper.filterByResources(resourcesFilter));
        assertThat(actual).hasSize(filteredScopeIds.length);
        assertThat(actual.stream().map(RBACScope::getId)).containsExactlyInAnyOrder(filteredScopeIds);
    }


    @Test
    void createScope() {
        RBACScope newScope = new RBACScope()
                .setName(newScopeName)
                .setResources(IntStream.range(0, newScopeResourcesNumber)
                        .mapToObj(i -> new RBACResource()
                                .setSelector("*")
                                .setResourceType(new RBACResourceType().setId(newScopeResourcesType)))
                        .collect(Collectors.toList()));
        RBACScope savedScope = scopeService.createScope(newScope);
        assertNotNull(savedScope);

        Optional<RBACScope> response = scopeService.getScopeById(savedScope.getId());
        assertTrue(response.isPresent());

        RBACScope fetchedScope = response.get();
        assertEquals(newScopeName, fetchedScope.getName());
        assertEquals(newScopeResourcesNumber, fetchedScope.getResources().size());

    }

    @Test
    void updateScope() {

        RBACScope newScope = new RBACScope()
                .setName(newScopeName)
                .setResources(IntStream.range(0, newScopeResourcesNumber)
                        .mapToObj(i -> new RBACResource()
                                .setSelector("*")
                                .setResourceType(new RBACResourceType().setId(newScopeResourcesType)))
                        .collect(Collectors.toList()));
        RBACScope savedScope = scopeService.createScope(newScope);
        assertNotNull(savedScope);

       String updatedName=newScopeName + "Updated";
       RBACScope updatedScope =  new RBACScope()
               .setId(savedScope.getId())
                       .setName(updatedName)
               .setResources(savedScope.getResources());
       scopeService.updateScope(updatedScope);

       Optional<RBACScope> response = scopeService.getScopeById(savedScope.getId());
       assertTrue(response.isPresent());

       RBACScope fetchedScope = response.get();
       assertEquals(updatedName, fetchedScope.getName());
    }

    @Test
    void deleteScope() {
        RBACScope newScope = new RBACScope()
                .setName(newScopeName)
                .setResources(IntStream.range(0, newScopeResourcesNumber)
                        .mapToObj(i -> new RBACResource()
                                .setSelector("*")
                                .setResourceType(new RBACResourceType().setId(newScopeResourcesType)))
                        .collect(Collectors.toList()));
        RBACScope savedScope = scopeService.createScope(newScope);
        assertNotNull(savedScope);

        Optional<RBACScope> response = scopeService.getScopeById(savedScope.getId());
        assertTrue(response.isPresent());

        scopeService.deleteScope(savedScope.getId());

        response = scopeService.getScopeById(savedScope.getId());
        assertTrue(response.isEmpty());
    }
}