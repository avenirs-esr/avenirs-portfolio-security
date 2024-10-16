package fr.avenirsesr.portfolio.security.services;

import fr.avenirsesr.portfolio.security.models.RBACContext;
import fr.avenirsesr.portfolio.security.models.Structure;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:db/test-fixtures-commons.sql",
        "classpath:db/test-fixtures-rbac-case1.sql",
        "classpath:db/test-fixtures-rbac-case2.sql"
})
@Transactional
class RBACContextServiceTest {

    @Value("${avenirs.test.rbac.context.service.context.number}")
    private int contextNumber;

    @Value("${avenirs.test.rbac.context.service.context.validity.start}")
    private String contextValidityStartString;

    @Value("${avenirs.test.rbac.context.service.context.validity.end}")
    private String contextValidityEndString;

    @Value("${avenirs.test.rbac.context.service.new.context.validity.start}")
    private String newContextValidityStartString;

    @Value("${avenirs.test.rbac.context.service.new.context.validity.end}")
    private String newContextValidityEndString;

    @Value("${avenirs.test.rbac.context.service.context.id}")
    private Long contextId;


    @Value("${avenirs.test.rbac.context.service.new.context.structure.names}")
    private String[] newContextStructureNames;

    private LocalDateTime contextValidityStart;
    private LocalDateTime contextValidityEnd;

    private LocalDateTime newContextValidityStart;
    private LocalDateTime newContextValidityEnd;

    @Autowired
    private RBACContextService contextService;

    @PostConstruct
    public void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        this.contextValidityStart = LocalDate.parse(contextValidityStartString, formatter).atStartOfDay();
        this.contextValidityEnd = LocalDate.parse(contextValidityEndString, formatter).atStartOfDay();
        this.newContextValidityStart =  LocalDate.parse(newContextValidityStartString, formatter).atStartOfDay();
        this.newContextValidityEnd = LocalDate.parse(newContextValidityEndString, formatter).atStartOfDay();
    }

    @Test
    void getContextsById() {

        Optional<RBACContext> response = contextService.getContextById(contextId);
        assertTrue(response.isPresent());
        RBACContext context = response.get();

        assertEquals(contextValidityStart, context.getValidityStart(), "Context validity start");
        assertEquals(contextValidityEnd, context.getValidityEnd(), "Context validity end");
        assertTrue(context.getStructures().isEmpty(),"No Structure");
    }

    @Test
    void getAllContexts() {
        List<RBACContext> contexts = contextService.getAllContexts();
        assertEquals(contextNumber, contexts.size(), "Context number");
    }

    @Test
    void createContext() {
        RBACContext newContext = new RBACContext()
                .setValidityStart(newContextValidityStart)
                .setValidityEnd(newContextValidityEnd)
                .setStructures(Arrays.stream(newContextStructureNames)
                        .map(name -> new Structure()
                                .setName(name)
                                .setDescription(""))
                        .collect(Collectors.toSet()));

        RBACContext savedContext = contextService.createContext(newContext);

        assertNotNull(savedContext);

        RBACContext fetchedContext =  contextService.getContextById(savedContext.getId())
                .orElseThrow(() -> new AssertionError("Context not found with ID: " + savedContext.getId()));

        assertEquals(newContextValidityStart, fetchedContext.getValidityStart());
        assertEquals(newContextValidityEnd, fetchedContext.getValidityEnd());

        assertThat(fetchedContext.getStructures())
                .as("New context structures")
                .extracting(Structure::getName)
                .containsExactlyInAnyOrder(newContextStructureNames);


    }

    @Test
    void updateContext() {
        RBACContext newContext = new RBACContext()
                .setValidityStart(newContextValidityStart)
                .setValidityEnd(newContextValidityEnd)
                .setStructures(Arrays.stream(newContextStructureNames)
                        .map(name -> new Structure()
                                .setName(name)
                                .setDescription(""))
                        .collect(Collectors.toSet()));

        RBACContext savedContext = contextService.createContext(newContext);
        assertNotNull(savedContext);

        RBACContext updatedContext = new RBACContext()
                .setId(savedContext.getId())
                .setValidityStart(newContextValidityStart.plusDays(1))
                .setValidityEnd(newContextValidityEnd)
                .setStructures(Collections.singleton(
                        new Structure()
                                .setName(newContextStructureNames[0])
                                .setDescription("")));

        updatedContext = contextService.updateContext(updatedContext);
        assertNotNull(updatedContext);

        RBACContext fetchedContext = contextService.getContextById(newContext.getId())
                .orElseThrow(() -> new AssertionError("Context not found with ID: " + newContext.getId()));

        assertEquals(newContextValidityStart.plusDays(1), fetchedContext.getValidityStart(), "Updated context validity start");
        assertEquals(newContextValidityEnd, fetchedContext.getValidityEnd(), "Updated context validity end");
        assertEquals(1, fetchedContext.getStructures().size());
        assertEquals(newContextStructureNames[0], fetchedContext.getStructures().toArray(new Structure[0])[0].getName(), "Updated context, structure name");

    }

    @Test
    void deleteContext() {

        RBACContext newContext = new RBACContext()
                .setValidityStart(newContextValidityStart)
                .setValidityEnd(newContextValidityEnd)
                .setStructures(Arrays.stream(newContextStructureNames)
                        .map(name -> new Structure()
                                .setName(name)
                                .setDescription(""))
                        .collect(Collectors.toSet()));

        RBACContext savedContext = contextService.createContext(newContext);
        assertNotNull(savedContext);

        Optional<RBACContext> response = contextService.getContextById(savedContext.getId());
        assertTrue(response.isPresent(), "Context present before delete");

        contextService.deleteContext(savedContext.getId());

        response = contextService.getContextById(savedContext.getId());
        assertTrue(response.isEmpty(), "Context deleted");
    }
}