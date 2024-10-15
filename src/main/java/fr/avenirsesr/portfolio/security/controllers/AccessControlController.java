package fr.avenirsesr.portfolio.security.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.AccessControlResponse;
import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRouteSpecificationHelper;
import fr.avenirsesr.portfolio.security.services.AccessControlService;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import fr.avenirsesr.portfolio.security.services.RBACActionRouteService;


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

    @GetMapping("${avenirs.access.control}")
    public ResponseEntity<AccessControlResponse> hasAccess(@RequestHeader(value = "x-authorization") String token,
                                                           @RequestParam String uri, @RequestParam String method,
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

                    boolean granted = accessControlService.hasAccess(login, action.getId(), resourceId);
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

}
