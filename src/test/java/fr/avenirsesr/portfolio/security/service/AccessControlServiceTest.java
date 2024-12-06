package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * <h1>AccessControlServiceTest</h1>
 * <p>
 * <b>Description:</b> based on test case 1: user gribonvald is pair for a resource of type MES.
 * * </p>
 * * For more details
 * * <a href="https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/">
 * *     https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/
 * * </a>
 * *
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 15/10/2024
 */

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:db/test-fixtures-commons.sql"
})
@Transactional
class AccessControlServiceTest {

    @Value("${avenirs.test.rbac.case2.user.login}")
    private String userLogin;

    @Value("${avenirs.test.rbac.case2.authorized.resource.id}")
    private UUID authorizedResourceId;

    @Value("${avenirs.test.rbac.case2.unauthorized.resource.id}")
    private UUID unauthorizedResourceId;

    @Value("${avenirs.test.access.control.service.action.display.id}")
    private UUID displayActionId;
    @Value("${avenirs.test.access.control.service.action.edit.id}")
    private UUID editActionId;

    @Value("${avenirs.test.access.control.service.action.feedback.id}")
    private UUID feedbackActionId;

    @Value("${avenirs.test.rbac.case2.application.context.validity.start}")
    private String validityStartString;

    @Value("${avenirs.test.access.control.service.grant.resource.ids}")
    private UUID[] grantResourceIds;

    @Value("${avenirs.test.access.control.service.grant.structure.ids}")
    private UUID[] grantStructureIds;

    @Value("${avenirs.test.access.control.service.grant.role.id}")
    private UUID grantRoleId;


    /**
     * execution date in valid date range.
     */
    private LocalDateTime effectiveDateInValidityRange;

    /**
     * Validity start date.
     */
    private LocalDateTime validityStart;

    /**
     * Validity end date.
     */
    private LocalDateTime validityEnd;


    @Value("${avenirs.access.control.date.format}")
    private String dateFormat;

    private DateTimeFormatter formatter;

    @SpyBean
    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private RBACAssignmentService assignmentService;

    @PostConstruct
    public void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        validityStart = LocalDate.parse(validityStartString, formatter).atStartOfDay();
        validityEnd = validityStart.plusDays(10);
        this.effectiveDateInValidityRange = validityStart.plusDays(1);
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql",
            "classpath:db/test-fixtures-rbac-case2.sql"
    })
    @Test
    void isAuthorized() {
        Principal principal = new Principal().setLogin(userLogin);
        RBACAction display = new RBACAction().setId(displayActionId);
        RBACAction edit = new RBACAction().setId(editActionId);
        RBACAction feedback = new RBACAction().setId(feedbackActionId);
        RBACResource authorizedResource = new RBACResource().setId(authorizedResourceId);
        RBACResource unauthorizedResource = new RBACResource().setId(unauthorizedResourceId);

        doAnswer(invocation -> {
            Principal p = invocation.getArgument(0);
            return new RBACContext()
                    .setStructures(p.getStructures())
                    .setEffectiveDate(effectiveDateInValidityRange);
        }).when(accessControlService).createExecutionContext(any(Principal.class));


        assertTrue(accessControlService.isAuthorized(principal.getLogin(), display.getId(), authorizedResource.getId()), "Display access on authorized resource.");
        assertFalse(accessControlService.isAuthorized(principal.getLogin(), display.getId(), unauthorizedResource.getId()), "Display access on unauthorized resource.");
        assertFalse(accessControlService.isAuthorized(principal.getLogin(), edit.getId(), authorizedResource.getId()), "Edit access on authorized resource.");
        assertFalse(accessControlService.isAuthorized(principal.getLogin(), edit.getId(), unauthorizedResource.getId()), "Edit access on unauthorized resource.");
        assertTrue(accessControlService.isAuthorized(principal.getLogin(), feedback.getId(), authorizedResource.getId()), "Feedback access on authorized resource.");
        assertFalse(accessControlService.isAuthorized(principal.getLogin(), feedback.getId(), unauthorizedResource.getId()), "Feedback access on unauthorized resource.");

    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccess() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        accessControlService.grantAccess(grantRequest);

        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "Assignment created after grant");

        RBACAssignment assignment = assignments.getFirst();
        assertEquals(userLogin, assignment.getPrincipal().getLogin(), "Assignment principal login");
        assertEquals(grantRoleId, assignment.getRole().getId(), "Assignment role id");
        assertThat(assignment.getScope().getResources().stream().map(RBACResource::getId)).containsExactlyInAnyOrder(grantResourceIds);
        assertEquals(validityStart, assignment.getContext().getValidityStart(), "Assignment validity start");
        assertEquals(validityEnd, assignment.getContext().getValidityEnd(), "Assignment validity end");
        assertThat(assignment.getContext().getStructures().stream().map(Structure::getId)).containsExactlyInAnyOrder(grantStructureIds);
    }


    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidPrincipal() {

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin("user123")
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accessControlService.grantAccess(grantRequest));

        assertEquals("Principal not found, UID: user123", exception.getMessage(), "Exception message");
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidRole() {

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(UUID.fromString("00000000-0000-0000-0000-000000000123"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accessControlService.grantAccess(grantRequest));

        assertEquals("Role not found, ID: 00000000-0000-0000-0000-000000000123", exception.getMessage(), "Exception message");
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessNoResource() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                //.setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        accessControlService.grantAccess(grantRequest);

        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "Assignment created after grant");

        RBACAssignment assignment = assignments.getFirst();
        assertEquals(userLogin, assignment.getPrincipal().getLogin(), "Assignment principal login");
        assertEquals(grantRoleId, assignment.getRole().getId(), "Assignment role id");
        assertTrue(assignment.getScope().getResources().isEmpty(), "No resource");
        assertEquals(validityStart, assignment.getContext().getValidityStart(), "Assignment validity start");
        assertEquals(validityEnd, assignment.getContext().getValidityEnd(), "Assignment validity end");
        assertThat(assignment.getContext().getStructures().stream().map(Structure::getId)).containsExactlyInAnyOrder(grantStructureIds);
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidResource() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        List<UUID> invalidResourceIds = new ArrayList<>(Arrays.asList(grantResourceIds));
        invalidResourceIds.add(UUID.fromString("00000000-0000-0000-0000-000000000100"));
        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(invalidResourceIds.toArray(new UUID[0]))
                .setRoleId(grantRoleId);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> accessControlService.grantAccess(grantRequest));
        assertEquals("Missing resources, IDs : [00000000-0000-0000-0000-000000000100]", exception.getMessage(), "Exception message for missing resources");
    }

    @Test
    void grantAccessNoStructure() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        accessControlService.grantAccess(grantRequest);

        assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "Assignment created after grant");

        RBACAssignment assignment = assignments.getFirst();
        assertEquals(userLogin, assignment.getPrincipal().getLogin(), "Assignment principal login");
        assertEquals(grantRoleId, assignment.getRole().getId(), "Assignment role id");
        assertThat(assignment.getScope().getResources().stream().map(RBACResource::getId)).containsExactlyInAnyOrder(grantResourceIds);
        assertEquals(validityStart, assignment.getContext().getValidityStart(), "Assignment validity start");
        assertEquals(validityEnd, assignment.getContext().getValidityEnd(), "Assignment validity end");
        assertTrue(assignment.getContext().getStructures().isEmpty(), "No structure");
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidStructure() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        List<UUID> invalidStructureIds = new ArrayList<>(Arrays.asList(grantStructureIds));
        invalidStructureIds.add(UUID.fromString("00000000-0000-0000-0000-000000000100"));
        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(invalidStructureIds.toArray(new UUID[0]))
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> accessControlService.grantAccess(grantRequest));
        assertEquals("Missing structures, IDs : [00000000-0000-0000-0000-000000000100]", exception.getMessage(), "Exception message for missing resources");
    }


    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidValidityStart() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.toString())
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        Exception exception = assertThrows(RuntimeException.class, () -> accessControlService.grantAccess(grantRequest));
        assertTrue(exception.getMessage().contains("Invalid validity start date format"), "Exception message for invalid start date format");
    }


    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void grantAccessWithInvalidValidityEnd() {

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "No assignment at startup");

        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.toString())
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        Exception exception = assertThrows(RuntimeException.class, () -> accessControlService.grantAccess(grantRequest));
        assertTrue(exception.getMessage().contains("Invalid validity end date format"), "Exception message for invalid end date format");
    }

    @Sql(scripts = {
            "classpath:db/test-fixtures-commons.sql"
    })
    @Test
    void revokeAccess() {


        AccessControlGrantRequest grantRequest = new AccessControlGrantRequest()
                .setLogin(userLogin)
                .setValidityStart(validityStart.format(formatter))
                .setValidityEnd(validityEnd.format(formatter))
                .setStructureIds(grantStructureIds)
                .setResourceIds(grantResourceIds)
                .setRoleId(grantRoleId);

        accessControlService.grantAccess(grantRequest);

        List<RBACAssignment> assignments = assignmentService.getAllAssignments();
        assertEquals(1, assignments.size(), "Assignment created after grant");
        RBACAssignment assignment = assignments.getFirst();

        AccessControlRevokeResponse response = accessControlService.revokeAccess(new AccessControlRevokeRequest()
                .setLogin(userLogin)
                .setAssignmentId(assignment.getId()));
        assertEquals(userLogin, response.getLogin(), "Response user login");
        assertTrue(response.isRevoked(), "Response revoked flag");
        assertNull(response.getError(), "Response without error");

        assignments = assignmentService.getAllAssignments();
        assertTrue(assignments.isEmpty(), "Assignment deleted");
    }

}