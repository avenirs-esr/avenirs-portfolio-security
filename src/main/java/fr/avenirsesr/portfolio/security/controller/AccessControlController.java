package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import fr.avenirsesr.portfolio.security.repository.RBACActionRouteSpecificationHelper;
import fr.avenirsesr.portfolio.security.service.AccessControlService;
import fr.avenirsesr.portfolio.security.service.AuthenticationService;
import fr.avenirsesr.portfolio.security.service.RBACActionRouteService;

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
public class AccessControlController {

    /**
     * Authentication service used to retrieve the user information.
     */
    @Autowired
    private AuthenticationService authenticationService;

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
     * Checks if a principal is authorized to perform an action on a resource.
     * The action is specified by an uri and an HTTP method.
     *
     * @param token      The token associated to the principal.
     * @param uri        The uri of the action.
     * @param method     The HTTP method of the action.
     * @param resourceId The involved resource.
     * @return The AccessControlResponse that contains the flag to determine if the action is authorized.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @GetMapping("${avenirs.access.control}")
    public ResponseEntity<AccessControlResponse> isAuthorized(@RequestHeader(value = "x-authorization") String token,
                                                              @RequestParam String uri,
                                                              @RequestParam String method,
                                                              @RequestParam(required = false, name = "resourceId") Long resourceId) {
        log.trace("hasAccess, token: {} ", token);
        log.trace("hasAccess, uri: {} ", uri);
        log.trace("hasAccess, method: {} ", method);
        log.trace("hasAccess, resourceId: {} ", resourceId);

        AccessControlResponse response = new AccessControlResponse()
                .setToken(token)
                .setResourceId(resourceId)
                .setUri(uri)
                .setMethod(method);
        if (StringUtils.hasLength(uri) && StringUtils.hasLength(method)) {
            RBACActionRoute actionRoute = actionRouteService.getAllActionRoutesBySpecification(
                    RBACActionRouteSpecificationHelper.filterByURIAndMethod(uri, method.toLowerCase())).orElse(null);
            log.trace("hasAccess, actionRoute: {} ", actionRoute);

            if (actionRoute != null) {

                RBACAction action = actionRoute.getAction();
                log.trace("hasAccess, action: {}", action);
                response.setActionName(action.getName());
                OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
                log.trace("hasAccess, introspectResponse: {}", introspectResponse);

                if (introspectResponse != null && introspectResponse.isActive()) {
                    String login = introspectResponse.getUniqueSecurityName();
                    log.trace("hasAccess, login: {}", login);
                    response.setLogin(login);

                    boolean granted = accessControlService.isAuthorized(login, action.getId(), resourceId);
                    log.trace("hasAccess, granted: {}", granted);

                    response.setGranted(granted);
                    return response.isGranted() ? ResponseEntity.ok(response)
                            : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
        }
        log.trace("hasAccess, response: {}", response);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    /**
     * Grant a role to a principal for a resource (optional) and an application context (optional).
     *
     * @param token   The token associated to the principal.
     * @param request grant request with the role, the resource and application context information.
     * @return An AccessControlGrantResponse.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @PostMapping("${avenirs.access.control.grant}")
    public ResponseEntity<AccessControlGrantResponse> grantAccess(@RequestHeader(value = "x-authorization") String token,
                                                                  @RequestBody AccessControlGrantRequest request) {
        log.trace("grantAccess, token: {}", token);
        log.trace("grantAccess, request: {}", request);

        OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
        log.trace("grantAccess, introspectResponse: {}", introspectResponse);

        if (introspectResponse != null && introspectResponse.isActive()) {
            AccessControlGrantResponse response;
            try {
                response = this.accessControlService.grantAccess(request.setUid(introspectResponse.getUniqueSecurityName()));
            } catch (Exception e) {
                response = new AccessControlGrantResponse()
                        .setLogin(introspectResponse.getUniqueSecurityName())
                        .setGranted(false)
                        .setError(e.getMessage());
            }
            return response.isGranted() ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);


    }


    /**
     * Revoke access granted to a principal on resource(s).
     *
     * @param token        The access token associated to the principal.
     * @param request The ids used to revoke the access.
     * @return An instance of AccessControlGrantResponse which contains the result of the operation.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @DeleteMapping("${avenirs.access.control.grant}")
    public ResponseEntity<AccessControlRevokeResponse> revokeAccess(@RequestHeader(value = "x-authorization") String token,
                                                                    @RequestBody AccessControlRevokeRequest request) {
        log.trace("revokeAccess, token: {}", token);
        log.trace("revokeAccess, request: {}", request);

        OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
        log.trace("revokeAccess, introspectResponse: {}", introspectResponse);

        if (introspectResponse != null && introspectResponse.isActive()) {
            AccessControlRevokeResponse response;
            try {
                response = this.accessControlService.revokeAccess(request.setUid(introspectResponse.getUniqueSecurityName()));
            } catch (Exception e) {
                response = new AccessControlRevokeResponse()
                        .setLogin(introspectResponse.getUniqueSecurityName())
                        .setRevoked(false)
                        .setError(e.getMessage());
            }
            return response.isRevoked() ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);


    }
}