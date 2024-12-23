package fr.avenirsesr.portfolio.security.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.security.model.*;
import fr.avenirsesr.portfolio.security.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACAssignmentServiceTest {

    @Autowired
    private RBACAssignmentService assignmentService;

    @Autowired
    private RBACRoleRepository roleRepository;

    @Autowired
    private PrincipalRepository principalRepository;

    @Autowired
    private RBACResourceRepository resourceRepository;

    @Value("${avenirs.test.rbac.assignment.service.role.owner.id}")
    private UUID roleOwnerId;

    @Value("${avenirs.test.rbac.assignment.service.resource.id.1}")
    private UUID resourceId1;

    @Value("${avenirs.test.rbac.assignment.service.resource.id.2}")
    private UUID resourceId2;

    @Value("${avenirs.test.rbac.assignment.service.resource.id.3}")
    private UUID resourceId3;

    @Value("${avenirs.test.rbac.assignment.service.user.login.1}")
    private String principalLogin1;

    @Value("${avenirs.test.rbac.assignment.service.user.login.2}")
    private String principalLogin2;


    private boolean initialized = false;

    private RBACRole owner;
    private Principal principal1;
    private Principal principal2;
    private RBACResource resource1;
    private RBACResource resource2;
    private RBACResource resource3;


    @BeforeEach
    public void setUp() {
        if (!initialized) {
            owner = roleRepository.findById(roleOwnerId).orElseThrow();
            principal1 = principalRepository.findByLogin(principalLogin1).orElseThrow();
            principal2 = principalRepository.findByLogin(principalLogin2).orElseThrow();
            resource1 = resourceRepository.findById(resourceId1).orElseThrow();
            resource2 = resourceRepository.findById(resourceId2).orElseThrow();
            resource3 = resourceRepository.findById(resourceId3).orElseThrow();
            initialized = true;
        }
    }

    @Test
    void getAllAssignmentsEmptyAtStartup() {
        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertEquals(0, assignments.size(), "No assignment at start up");
    }


    @Test
    void createAssignment() {
        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertEquals(0, assignments.size(), "No assignment at start up");

        RBACScope scope = new RBACScope()
                .setName("createAssignment Scope")
                //.setId(UUID.fromString("00000000-0000-0000-0000-000000000100"))
                .setResources(Collections.singletonList(resource1));
        RBACContext context = new RBACContext().setId(UUID.fromString("00000000-0000-0000-0000-000000000100"));

        RBACAssignment assignment = new RBACAssignment()
                .setContext(context)
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(scope);

        this.assignmentService.createAssignment(assignment);

        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "One assignment registered");

        RBACAssignment fetchedAssignment = assignments.getFirst();
        assertEquals(principal1.getId(), fetchedAssignment.getPrincipal().getId(), "Principal Id");
        assertNull(fetchedAssignment.getContext().getValidityStart(), "Context validity start");
        assertNull(fetchedAssignment.getContext().getValidityEnd(), "Context validity end");
        assertTrue(fetchedAssignment.getContext().getStructures().isEmpty(), "Context empty structures");

        assertEquals(scope.getName(), fetchedAssignment.getScope().getName(), "Scope name");


    }

    @Test
    void updateAssignment() {
        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertEquals(0, assignments.size(), "No assignment at start up");

        RBACScope scope = new RBACScope()
                .setName("updateAssignment Initial Value")
                .setResources(Collections.singletonList(resource1));
        RBACContext context = new RBACContext();

        RBACAssignment assignment = new RBACAssignment()
                .setContext(context)
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(scope);
        assignment = assignmentService.createAssignment(assignment);

        assignments = assignmentService.getAllAssignments();

        assertEquals(1, assignments.size(), "One assignment after creation");

        final String updatedScopeName = "updateAssignment Updated Value";

        RBACScope updatedScope = new RBACScope()
                .setId(assignment.getScope().getId())
                .setName(updatedScopeName)
                .setResources(assignment.getScope().getResources());

        RBACAssignment updatedAssignment = new RBACAssignment()
                .setId(assignment.getId())
                .setContext(assignment.getContext())
                .setRole(assignment.getRole())
                .setPrincipal(assignment.getPrincipal())
                .setScope(updatedScope);


        assignmentService.updateAssignment(updatedAssignment);
        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "One assignment after update");

        RBACAssignment fetchedAssignment = assignments.getFirst();
        assertEquals(updatedScopeName, fetchedAssignment.getScope().getName(), "Scope name updated");
    }

    @Test
    void deleteAssignment() {
        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertEquals(0, assignments.size(), "No assignment at start up");

        RBACScope scope = new RBACScope()
                .setName("createAssignment Scope")
                .setResources(Collections.singletonList(resource1));
        RBACContext context = new RBACContext();

        RBACAssignment assignment = new RBACAssignment()
                .setContext(context)
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(scope);
        assignment = assignmentService.createAssignment(assignment);

        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "One assignment after create");

        assignmentService.deleteAssignment(assignment.getId());

        assignments = assignmentService.getAllAssignments();
        assertEquals(0, assignments.size(), "No assignment after deleteById");


    }

    @Test
    void getAllAssignmentsBySpecificationForPrincipal() {

        assertEquals(0, assignmentService.getAllAssignments().size(), "No assignment at start up");

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope()
                        .setResources(Collections.singletonList(resource1))));

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource3))));

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal2)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource2))));
        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal2)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource3))));

        assertEquals(4, assignmentService.getAllAssignments().size(), "Assignments after inserts");

        // Checks assignments for principal 1
        List<RBACAssignment> fetchedAssignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipal(principalLogin1));
        assertEquals(2, fetchedAssignments.size(), "Number of fetched Assignments for principal 1");
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for principal1")
                .extracting(assignment -> assignment.getPrincipal().getLogin())
                .allMatch(login -> login.equals(principalLogin1));

        // Checks assignments for principal 2
        fetchedAssignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipal(principalLogin2));
        assertEquals(2, fetchedAssignments.size(), "Number of fetched Assignments for principal 2");
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for principal2")
                .extracting(assignment -> assignment.getPrincipal().getLogin())
                .allMatch(login -> login.equals(principalLogin2));

    }

    @Test
    void getAllAssignmentsBySpecificationPrincipalAndResources() {

        assertEquals(0, assignmentService.getAllAssignments().size(), "No assignment at start up");

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope()
                        .setResources(Collections.singletonList(resource1))));

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope()
                        .setResources(Arrays.asList(resource1, resource2))));

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope()
                        .setResources(Arrays.asList(resource1, resource2, resource3))));


        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource2))));

        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal1)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource3))));


        assignmentService.createAssignment(new RBACAssignment()
                .setContext(new RBACContext())
                .setRole(owner)
                .setPrincipal(principal2)
                .setScope(new RBACScope().setResources(Collections.singletonList(resource3))));

        // Checks assignments for principal 1 and resource 1
        List<RBACAssignment> fetchedAssignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipalAndResources(principalLogin1, resourceId1));
        assertEquals(3, fetchedAssignments.size(), "Number of fetched Assignments for principal 1 and resource 1");
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for principal1")
                .extracting(assignment -> assignment.getPrincipal().getLogin())
                .allMatch(login -> login.equals(principalLogin1));

        // Resource
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for resource 1")
                .extracting(assignment -> assignment.getScope().getResources())
                .allSatisfy(resources ->
                        assertThat(resources)
                                .extracting(RBACResource::getId)
                                .anyMatch(resourceId -> resourceId.equals(resourceId1))
                );


        // Checks assignments for principal 1 and resource 1 or 2
        fetchedAssignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipalAndResources(principalLogin1, resourceId1, resourceId2));
        assertEquals(4, fetchedAssignments.size(), "Number of fetched Assignments for principal 1 and resource 1 and 2");
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for principal1")
                .extracting(assignment -> assignment.getPrincipal().getLogin())
                .allMatch(login -> login.equals(principalLogin1));

        // Checks resources
        List<UUID> expectedResourceIds = Arrays.asList(resourceId1, resourceId2);
        assertThat(fetchedAssignments)
                .as("Checks that all assignments are for resource 1 or 2")
                .extracting(assignment -> assignment.getScope().getResources())
                .allSatisfy(resources ->
                        assertThat(resources)
                                .extracting(RBACResource::getId)
                                .anyMatch(expectedResourceIds::contains)
                );

    }

}
