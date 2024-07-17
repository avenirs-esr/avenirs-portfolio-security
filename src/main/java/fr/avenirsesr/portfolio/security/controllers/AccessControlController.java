package fr.avenirsesr.portfolio.security.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.AccessControlResponse;
import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRouteSpecification;
import fr.avenirsesr.portfolio.security.services.AccessControlService;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import fr.avenirsesr.portfolio.security.services.RBACActionRouteService;

@RestController
public class AccessControlController {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlController.class);

	/** Authentication service used to retrieve the user informations. */
	@Autowired
	private AuthenticationService authenticationService;

	/** Action route service. */
	@Autowired
	private RBACActionRouteService actionRouteService;

	/** Access control service. */
	@Autowired
	private AccessControlService accessControlService;

	@GetMapping("${avenirs.accessControl}")
	public AccessControlResponse hasAccess(@RequestHeader(value = "x-authorization") String token,
			@RequestParam String uri, @RequestParam String method,
			@RequestParam(required = false, name = "resource") Long resourceId) {
		LOGGER.trace("hasAccess, token: {} ", token);
		LOGGER.trace("hasAccess, uri: {} ", uri);
		LOGGER.trace("hasAccess, method: {} ", method);
		LOGGER.trace("hasAccess, resourceId: {} ", resourceId);

		AccessControlResponse response = new AccessControlResponse().setToken(token).setResourceId(resourceId)
				.setUri(uri).setMethod(method);
		if (StringUtils.hasLength(uri) && StringUtils.hasLength(method)) {
			RBACActionRoute actionRoute = actionRouteService.getActionRouteByPredicate(
					RBACActionRouteSpecification.filterByURIAndMethod(uri, method.toLowerCase())
				).orElse(null);
			LOGGER.trace("hasAccess, actionRoute: {} ", actionRoute);

			if (actionRoute != null) {

				RBACAction action = actionRoute.getAction();
				LOGGER.trace("hasAccess, action: {}", action);
				response.setActionName(action.getName());
				OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
				LOGGER.trace("hasAccess, introspectResponse: {}", introspectResponse);

				if (introspectResponse != null && introspectResponse.isActive()) {
					String login = introspectResponse.getUniqueSecurityName();
					LOGGER.trace("hasAccess, login: {}", login);
					response.setLogin(login);

					boolean granted = accessControlService.hasAccess(login, action.getId(), resourceId);
					LOGGER.trace("hasAccess, granted: {}", granted);

					response.setGranted(granted);
				}
			}
		}
		LOGGER.trace("hasAccess, response: {}", response);

		return response;
	}

}
