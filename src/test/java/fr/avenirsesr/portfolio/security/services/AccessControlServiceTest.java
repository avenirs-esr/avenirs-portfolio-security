package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACContext;
import fr.avenirsesr.portfolio.security.models.RBACResource;
import jakarta.annotation.PostConstruct;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * <h1>AccessControlServiceTest</h1>
 * <p>
 * <b>Description:</b> based on test case 1: user gribonvald is pair for a resource of type MES.
 *  * </p>
 *  * For more details
 *  * <a href="https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/">
 *  *     https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/
 *  * </a>
 *  *
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
@Sql(scripts={
        "classpath:db/test-fixtures-commons.sql",
        "classpath:db/test-fixtures-rbac-case2.sql"
})
@Transactional
class AccessControlServiceTest {

    @Value("${avenirs.test.rbac.case2.user.login}")
    private String userLogin;

    @Value("${avenirs.test.rbac.case2.authorized.resource.id}")
    private Long authorizedResourceId;

    @Value("${avenirs.test.rbac.case2.unauthorized.resource.id}")
    private Long unauthorizedResourceId;

    @Value("${avenirs.test.access.control.service.action.display.id}")
    private Long displayActionId;
    @Value("${avenirs.test.access.control.service.action.edit.id}")
    private Long editActionId;

    @Value("${avenirs.test.access.control.service.action.feedback.id}")
    private Long feedbackActionId;

    @Value("${avenirs.test.rbac.case2.application.context.validity.start}")
    private String validityStartString;

    /** execution date in valid date range. */
    private LocalDateTime effectiveDateInValidityRange;

    @SpyBean
    @Autowired
    private AccessControlService accessControlService;

    @PostConstruct
    public void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime validityStart = LocalDate.parse(validityStartString, formatter).atStartOfDay();
        this.effectiveDateInValidityRange = validityStart.plusDays(1);
    }

    @Test
    void hasAccess() {
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


        assertTrue (accessControlService.hasAccess(principal, display, authorizedResource), "Display access on authorized resource.");
        assertFalse (accessControlService.hasAccess(principal, display, unauthorizedResource), "Display access on unauthorized resource.");
        assertFalse (accessControlService.hasAccess(principal, edit, authorizedResource), "Edit access on authorized resource.");
        assertFalse (accessControlService.hasAccess(principal, edit, unauthorizedResource), "Edit access on unauthorized resource.");
        assertTrue (accessControlService.hasAccess(principal, feedback, authorizedResource), "Feedback access on authorized resource.");
        assertFalse (accessControlService.hasAccess(principal, feedback, unauthorizedResource), "Feedback access on unauthorized resource.");

    }

    @Test
    void testHasAccess() {
    }

    @Test
    void testHasAccess1() {
    }

    @Test
    void createExecutionContext() {
    }
}