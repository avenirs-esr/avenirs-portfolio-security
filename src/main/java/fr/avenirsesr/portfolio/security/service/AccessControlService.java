/**
 *
 */
package fr.avenirsesr.portfolio.security.service;

import java.time.LocalDate;
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

    @Value("${avenirs.access.control.date.format}")
    private String dateFormat;

    private DateTimeFormatter formatter;

    /**
     * Cache for the permissions.
     */
    private final Map<UUID, List<RBACPermission>> permissionsByActionId = new HashMap<>();


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

        Principal principal = principalService.getPrincipalByLogin(grantRequest.getLogin())
                .orElseThrow(() -> new EntityNotFoundException("Principal not found, UID: " + grantRequest.getLogin()));

        log.trace("grantAccess, role: {}", role);
        log.trace("grantAccess, principal: {}", principal);

        // Retrieve and check resources.
        List<RBACResource> resources;
        if (!ObjectUtils.isEmpty(grantRequest.getResourceIds())) {
            resources = resourceService.getAllResourcesBySpecification(
                    RBACResourceSpecificationHelper.filterByIds(grantRequest.getResourceIds()));

            List<UUID> foundIds = resources.stream()
                    .map(RBACResource::getId)
                    .toList();

            List<UUID> missingIds = Arrays.stream(grantRequest.getResourceIds())
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

        // Retrieves and checks the structures.
        if (!ObjectUtils.isEmpty(grantRequest.getStructureIds())) {
            List<Structure> structures = structureService.getAllStructuresBySpecification(StructureSpecificationHelper.filterByIds(grantRequest.getStructureIds()));

            List<UUID> foundIds = structures.stream()
                    .map(Structure::getId)
                    .toList();

            List<UUID> missingIds = Arrays.stream(grantRequest.getStructureIds())
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

        RBACAssignment registeredAssignment = assignmentService.createAssignment(assignment);


        return new AccessControlGrantResponse()
                .setLogin(grantRequest.getLogin())
                .setAssignmentId(registeredAssignment.getId())
                .setGranted(true);
    }


    /**
     * Revokes access by deleting the underlying assignment.
     * @param revokeRequest The information to determine the assignment to delete.
     * @return The response with the result of the operation.
     */
    public AccessControlRevokeResponse revokeAccess(AccessControlRevokeRequest revokeRequest) {
        log.trace("revokeAccess, revokeRequest: {}", revokeRequest);

        Principal principal = principalService.getPrincipalByLogin(revokeRequest.getLogin())
                .orElseThrow(() -> new EntityNotFoundException("Principal not found, UID: " + revokeRequest.getLogin()));

        RBACAssignmentPK assignmentId = new RBACAssignmentPK()
                .setPrincipal(principal.getId())
                .setContext(revokeRequest.getContextId())
                .setScope(revokeRequest.getScopeId())
                .setRole(revokeRequest.getRoleId());

        assignmentService.deleteAssignment(assignmentId);
        return new AccessControlRevokeResponse()
                .setLogin(revokeRequest.getLogin())
                .setAssignmentId(assignmentId)
                .setRevoked(true);
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
    public boolean isAuthorized(String login, UUID actionId, UUID resourceId) {
        log.trace("isAuthorized, login: {}", login);
        log.trace("isAuthorized, actionId: {}", actionId);
        log.trace("isAuthorized, resourceId: {}", resourceId);

       Optional<Principal> response = this.principalService.getPrincipalByLogin(login);

         if (response.isEmpty()){
             log.debug("isAuthorized, principal not found for login: {}", login);
             return false;
         }
         Principal principal = response.get();
         log.trace("isAuthorized, principal: {}", principal);

         RBACContext executionContext = createExecutionContext(principal);
        log.trace("isAuthorized, executionContext: {}", executionContext);

        List<RBACAssignment> principalAssignments = this.assignmentService.getAllAssignmentsBySpecification(
                RBACAssignmentSpecificationHelper.filterByPrincipalContextAndResources(login, executionContext, resourceId));
        log.trace("isAuthorized, principalAssignments: {}", principalAssignments);


        HashSet<RBACPermission> principalPermissions = principalAssignments.stream().map(a -> a.getRole().getPermissions())
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));
        log.trace("isAuthorized, principalPermissions: {}", principalPermissions);
        if (principalPermissions.isEmpty()){
            log.debug("isAuthorized,  principal {} has no permission", login);
            return false;
        }
        List<RBACPermission> requiredPermissions = this.fetchPermissions(actionId);
        log.trace("isAuthorized, requiredPermissions: {}", requiredPermissions);

        boolean accessGranted = principalPermissions.containsAll(requiredPermissions);
        log.trace("isAuthorized, accessGranted: {}", accessGranted);

        return accessGranted;

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


    /**
     * Fetches the permissions associated to an action.
     *
     * @param actionId The id of the action.
     * @return The permission associated to the action or null if the action is not
     * found.
     */
    private List<RBACPermission> fetchPermissions(UUID actionId) {

        if (actionId != null && !this.permissionsByActionId.containsKey(actionId)) {

            RBACAction action = this.actionService.getActionById(actionId).orElse(null);
            List<RBACPermission> permissions = action == null ? null : action.getPermissions();
            permissionsByActionId.put(actionId, permissions);
        }
        return permissionsByActionId.get(actionId);

    }


}
