/**
 *
 */
package fr.avenirsesr.portfolio.security.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import fr.avenirsesr.portfolio.security.model.*;
import fr.avenirsesr.portfolio.security.repository.RBACResourceSpecificationHelper;
import fr.avenirsesr.portfolio.security.repository.StructureSpecificationHelper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import fr.avenirsesr.portfolio.security.repository.RBACAssignmentSpecificationHelper;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <h1>AccessControlService</h1>
 * <p>
 * <b>Description:</b> AccessControlService is used to check if a resource can be accessed by a principal.
 * It is an RBAC: Role Based Access Control.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 04/10/2024
 */
@Slf4j
@Service
public class AccessControlService {

    /**
     * Action service
     */
    @Autowired
    RBACActionService actionService;

    /**
     * Assignment service.
     */
    @Autowired
    RBACAssignmentService assignmentService;

    @Autowired
    RBACResourceService resourceService;

    @Autowired
    PrincipalService principalService;

    @Autowired
    RBACRoleService roleService;

    @Autowired
    StructureService structureService;

    @Autowired
    RBACContextService contextService;

    @Value("${avenirs.access.control.date.format}")
    private String dateFormat;

    private DateTimeFormatter formatter;

    /**
     * Cache for the permissions.
     */
    private final Map<Long, List<RBACPermission>> permissionsByActionId = new HashMap<>();


    @PostConstruct
    public void init() {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    /**
     * Grant access.
     *
     * @param grantRequest The request with the information on the access to grant.
     * @return An AccessControlResponse.
     */
    public AccessControlGrantResponse grantAccess(AccessControlGrantRequest grantRequest) {
        log.trace("grantAccess, grantRequest: {}", grantRequest);


        // Role and principal.
        RBACRole role = roleService.getRoleById(grantRequest.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found, ID: " + grantRequest.getRoleId()));

        Principal principal = principalService.getPrincipalByLogin(grantRequest.getUid())
                .orElseThrow(() -> new EntityNotFoundException("Principal not found, UID: " + grantRequest.getUid()));

        log.trace("grantAccess, role: {}", role);
        log.trace("grantAccess, principal: {}", principal);

        // Retrieve and check resources.
        List<RBACResource> resources;
        if (!ObjectUtils.isEmpty(grantRequest.getResourceIds())) {
            resources = resourceService.getAllResourcesBySpecification(
                    RBACResourceSpecificationHelper.filterByIds(grantRequest.getResourceIds()));

            List<Long> foundIds = resources.stream()
                    .map(RBACResource::getId)
                    .toList();

            List<Long> missingIds = Arrays.stream(grantRequest.getResourceIds())
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                log.warn("Missing resource ids: {}", missingIds);
                throw new EntityNotFoundException("Missing resources, IDs : " + missingIds);
            }
        } else {
            resources = Collections.emptyList();
        }
        log.trace("grantAccess, resource: {}", resources);


        // Generates an application context.
        RBACContext applicationContext = new RBACContext();

        if (StringUtils.hasLength(grantRequest.getValidityStart())) {
            try {
                applicationContext.setValidityStart(LocalDate.parse(grantRequest.getValidityStart(), formatter).atStartOfDay());
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid validity start date format (" + dateFormat + "): " + grantRequest.getValidityStart());
            }
        }

        if (StringUtils.hasLength(grantRequest.getValidityEnd())) {
            try {
                applicationContext.setValidityEnd(LocalDate.parse(grantRequest.getValidityEnd(), formatter).atStartOfDay());
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid validity end date format (" + dateFormat + "): " + grantRequest.getValidityEnd());
            }
        }

        // Retrieve and checks the structures.
        if (!ObjectUtils.isEmpty(grantRequest.getStructureIds())) {
            List<Structure> structures = structureService.getAllStructuresBySpecification(StructureSpecificationHelper.filterByIds(grantRequest.getStructureIds()));

            List<Long> foundIds = structures.stream()
                    .map(Structure::getId)
                    .toList();

            List<Long> missingIds = Arrays.stream(grantRequest.getStructureIds())
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                log.warn("Missing structures ids: {}", missingIds);
                throw new EntityNotFoundException("Missing structures, IDs : " + missingIds);
            }
            applicationContext.setStructures(new HashSet<>(structures));
        } else {
            applicationContext.setStructures(new HashSet<>());
        }

        RBACAssignment assignment = new RBACAssignment()
                .setScope(new RBACScope().setResources(resources))
                .setRole(role)
                .setPrincipal(principal)
                .setContext(applicationContext);

        assignmentService.createAssignment(assignment);


        return new AccessControlGrantResponse()
                .setLogin(grantRequest.getUid())
                .setGranted(true);
    }


    /**
     * Revokes access by deleting the underlying assignment.
     * @param revokeRequest The information to determine the assignment to delete.
     * @return The response with the result of the operation.
     */
    public AccessControlRevokeResponse revokeAccess(AccessControlRevokeRequest revokeRequest) {
        log.trace("revokeAccess, revokeRequest: {}", revokeRequest);

        Principal principal = principalService.getPrincipalByLogin(revokeRequest.getUid())
                .orElseThrow(() -> new EntityNotFoundException("Principal not found, UID: " + revokeRequest.getUid()));

        RBACAssignmentPK key = new RBACAssignmentPK()
                .setPrincipal(principal.getId())
                .setContext(revokeRequest.getContextId())
                .setScope(revokeRequest.getScopeId())
                .setRole(revokeRequest.getRoleId());

        assignmentService.deleteAssignment(key);
        return new AccessControlRevokeResponse()
                .setLogin(revokeRequest.getUid())
                .setRevoked(true);
    }

    /**
     * Checks that a principal has access to a resource to perform an action,
     * without application context.
     *
     * @param principal The principal.
     * @param action    The action to perform.
     * @param resource  The accessed resource.
     * @return True if the principal has access to the resource.
     */
    public boolean isAuthorized(Principal principal, RBACAction action, RBACResource resource) {
        log.trace("hasAccess, principal: {}", principal);
        log.trace("hasAccess, action: {}", action);
        log.trace("hasAccess, resource: {}", resource);

        return isAuthorized(principal.getLogin(), action.getId(), resource.getId());
    }

    /**
     * Checks that a principal has access to a resource to perform an action,
     * without application context.
     *
     * @param login      The login of the principal.
     * @param actionId   The id of the action to perform.
     * @param resourceId The id of the accessed resource.
     * @return True if the principal has access to the resource.
     */
    public boolean isAuthorized(String login, Long actionId, Long resourceId) {
        log.trace("hasAccess, login: {}", login);
        log.trace("hasAccess, actionId: {}", actionId);
        log.trace("hasAccess, resourceId: {}", resourceId);

        List<RBACPermission> requiredPermissions = this.fetchPermissions(actionId);
        log.trace("hasAccess, requiredPermissions: {}", requiredPermissions);

        return checkGrantedPermissions(requiredPermissions, login, resourceId);
    }

    /**
     * Checks the granted permission for a user regarding the required ones.
     *
     * @param requiredPermissions The required permissions.
     * @param login               The login of the user.
     * @param resourceId          The resource id.
     * @return True if the permissions granted to the user for the resource contains all the required permissions.
     */
    private boolean checkGrantedPermissions(List<RBACPermission> requiredPermissions, String login, Long resourceId) {
        if (requiredPermissions != null && !requiredPermissions.isEmpty()) {

            List<RBACAssignment> principalAssignments = this.assignmentService.getAllAssignmentsBySpecification(
                    RBACAssignmentSpecificationHelper.filterByPrincipalAndResources(login, resourceId));
            log.trace("hasAccess, principalAssignments: {}", principalAssignments);

            List<RBACAssignment> validAssignment = filterByApplicationContext(principalAssignments);
            log.trace("hasAccess, validAssignment: {}", validAssignment);

            HashSet<RBACPermission> principalPermissions = validAssignment.stream().map(a -> a.getRole().getPermissions())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(HashSet::new));
            log.trace("hasAccess, principalPermissions: {}", principalPermissions);

            boolean accessGranted = principalPermissions.containsAll(requiredPermissions);
            log.trace("hasAccess, accessGranted: {}", accessGranted);

            return accessGranted;
        }
        return false;
    }

    private List<RBACAssignment> filterByApplicationContext(List<RBACAssignment> principalAssignments) {
        log.trace("filterByApplicationContext, principalAssignments: {}", principalAssignments);

        if (CollectionUtils.isEmpty(principalAssignments)) {
            return new ArrayList<>();
        }
        Principal principal = principalAssignments.getFirst().getPrincipal();
        RBACContext executionContext = createExecutionContext(principal);
        return principalAssignments.stream()
                .filter(a -> checkExecutionContext(a.getContext(), executionContext))
                .collect(Collectors.toList());
    }

    /**
     * Creates an execution context.
     *
     * @param principal The Principal associated to the execution context.
     * @return The execution context.
     */
    public RBACContext createExecutionContext(Principal principal) { // Need to be public to be mocked in tests.
        return new RBACContext().setStructures(principal.getStructures());
    }

    private boolean checkExecutionContext(RBACContext applicationContext, RBACContext executionContext) {
        log.trace("checkExecutionContext, applicationContext: {}", applicationContext);
        log.trace("checkExecutionContext, executionContext: {}", executionContext);

        if (applicationContext != null) {
            LocalDateTime validityStart = applicationContext.getValidityStart();
            LocalDateTime validityEnd = applicationContext.getValidityEnd();

            log.trace("checkExecutionContext, validityStart: {}", validityStart);
            log.trace("checkExecutionContext, validityEnd: {}", validityEnd);
            log.trace("checkExecutionContext, executionContext.getEffectiveDate(): {}", executionContext.getEffectiveDate());

            if (validityStart != null && executionContext.getEffectiveDate().isBefore(validityStart)) {
                log.trace("checkExecutionContext, validityStart not respected");
                return false;
            }

            if (validityEnd != null && executionContext.getEffectiveDate().isAfter(validityEnd)) {
                log.trace("checkExecutionContext, validityEnd not respected");
                return false;
            }
            Set<Structure> exContextStructures = executionContext.getStructures();
            Set<Structure> appContextStructures = applicationContext.getStructures();
            log.trace("checkExecutionContext, exContextStructures: {}", exContextStructures);
            log.trace("checkExecutionContext, appContextStructures: {}", appContextStructures);

            if (CollectionUtils.isEmpty(appContextStructures)) {
                log.trace("checkExecutionContext, no structure in application context");
                return true;
            }

            if (CollectionUtils.isEmpty(exContextStructures) || !exContextStructures.containsAll(appContextStructures)) {
                log.trace("checkExecutionContext, structures of application context not respected");
                return false;
            }

            log.trace("checkExecutionContext, structures of application context respected");
            return true;
        }

        return true;
    }

    /**
     * Fetches the permissions associated to an action.
     *
     * @param actionId The id of the action.
     * @return The permission associated to the action or null if the action is not
     * found.
     */
    private List<RBACPermission> fetchPermissions(Long actionId) {

        if (actionId != null && !this.permissionsByActionId.containsKey(actionId)) {

            RBACAction action = this.actionService.getActionById(actionId).orElse(null);
            List<RBACPermission> permissions = action == null ? null : action.getPermissions();
            permissionsByActionId.put(actionId, permissions);
        }
        return permissionsByActionId.get(actionId);

    }


}
