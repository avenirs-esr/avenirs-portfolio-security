package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.delegate.SecurityDelegate;
import fr.avenirsesr.portfolio.security.model.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import fr.avenirsesr.portfolio.security.repository.RBACActionRouteSpecificationHelper;
import fr.avenirsesr.portfolio.security.service.AccessControlService;
import fr.avenirsesr.portfolio.security.service.RBACActionRouteService;

import java.util.UUID;

/**
 * <h1>AccessControlController</h1>
 * <p>
 * <b>Description:</b> AccessControlController is used to:
 * <ul>
 *     <li>Create assignment to grant privileges</li>
 *     <li>Update assignment to modify granted privileges</li>
 *     <li>Delete assignment to revoke privileges</li>
 *     <li>Check assignment to determine if an action is authorized for a principal</li>
 * </ul>
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 17/10/2024
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
public class AccessControlController {

    /**
     * Action route service.
     */
    @Autowired
    private RBACActionRouteService actionRouteService;

    /**
     * Access control service.
     */
    @Autowired
    private AccessControlService accessControlService;

    /**
     * Delegate for the security checks.
     */
    @Autowired
    private SecurityDelegate securityDelegate;


    /**
     * Checks if a principal is authorized to perform an action on a resource.
     * The action is specified by an uri and an HTTP method.
     *
     * @param uri        The uri of the action.
     * @param method     The HTTP method of the action.
     * @param resourceId The involved resource.
     * @return The AccessControlResponse that contains the flag to determine if the action is authorized.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @GetMapping("${avenirs.access.control.authorize}")
    public ResponseEntity<AccessControlResponse> isAuthorized(@RequestParam String uri,
                                                              @RequestParam String method,
                                                              @RequestParam(required = false, name = "resourceId") UUID resourceId) {
        String login = securityDelegate.getAuthenticatedUserLogin();
        log.trace("isAuthorized, login: {} ", login);

        log.trace("isAuthorized, uri: {} ", uri);
        log.trace("isAuthorized, method: {} ", method);
        log.trace("isAuthorized, resourceId: {} ", resourceId);


        AccessControlResponse response = new AccessControlResponse()
                .setLogin(login)
                .setResourceId(resourceId)
                .setUri(uri)
                .setMethod(method);
        if (StringUtils.hasLength(uri) && StringUtils.hasLength(method)) {
            RBACActionRoute actionRoute = actionRouteService.getAllActionRoutesBySpecification(
                    RBACActionRouteSpecificationHelper.filterByURIAndMethod(uri, method.toLowerCase())).orElse(null);
            log.trace("isAuthorized, actionRoute: {} ", actionRoute);

            if (actionRoute != null) {

                RBACAction action = actionRoute.getAction();
                log.trace("isAuthorized, action: {}", action);
                response.setActionName(action.getName());

                boolean granted = accessControlService.isAuthorized(login, action.getId(), resourceId);
                log.trace("isAuthorized, granted: {}", granted);

                response.setGranted(granted);
                return response.isGranted() ? ResponseEntity.ok(response)
                        : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }
        log.trace("isAuthorized, response: {}", response);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Grant a role to a principal for a resource (optional) and an application context (optional).
     *
     * @param request grant request with the role, the resource and application context information.
     * @return An AccessControlGrantResponse.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @PostMapping("${avenirs.access.control.grant}")
    public ResponseEntity<AccessControlGrantResponse> grantAccess(@RequestBody AccessControlGrantRequest request) {

        String login = securityDelegate.getAuthenticatedUserLogin();
        log.trace("grantAccess, login: {}", login);
        log.trace("grantAccess, request: {}", request);
        AccessControlGrantResponse response;
        try {
            response = this.accessControlService.grantAccess(request.setLogin(login));
        } catch (Exception exception) {
            log.error("grantAccess, exception:  {}", exception.getMessage());
            response = new AccessControlGrantResponse()
                    .setLogin(login)
                    .setGranted(false)
                    .setError(exception.getMessage());
        }
        log.debug("grantAccess, response: {}", response);
        return response.isGranted() ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);


    }


    /**
     * Revoke access granted to a principal on resource(s).
     *
     * @param request The ids used to revoke the access.
     * @return An instance of AccessControlGrantResponse which contains the result of the operation.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @DeleteMapping("${avenirs.access.control.grant}")
    public ResponseEntity<AccessControlRevokeResponse> revokeAccess(@RequestBody AccessControlRevokeRequest request) {

        String login = securityDelegate.getAuthenticatedUserLogin();
        log.trace("grantAccess, login: {}", login);

        log.trace("revokeAccess, request: {}", request);


        AccessControlRevokeResponse response;
        try {
            response = this.accessControlService.revokeAccess(request.setLogin(login));
        } catch (Exception exception) {
            log.error("revokeAccess, exception:  {}", exception.getMessage());

            response = new AccessControlRevokeResponse()
                    .setLogin(login)
                    .setRevoked(false)
                    .setError(exception.getMessage());
        }
        log.debug("revokeAccess, response: {}", response);
        return response.isRevoked() ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}