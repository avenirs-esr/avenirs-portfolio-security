package fr.avenirsesr.portfolio.security.controllers;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecificationHelper;
import fr.avenirsesr.portfolio.security.services.RBACAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    /**
     * Authentication service used to retrieve the user information.
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Assignment service, used to retrieve roles assigned to a principal for instance.
     */
    @Autowired
    private RBACAssignmentService assignmentService;


    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @GetMapping("${avenirs.access.control.roles}")
    public List<String> getRoles() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        String uid = authentication.getName();
        log.trace("getRoles, uid: {}", uid);
        List<RBACAssignment> assignments = assignmentService.getAllAssignmentsBySpecification(RBACAssignmentSpecificationHelper.filterByPrincipal(uid));

        List<String> roles = assignments.stream()
                .map(assignment -> assignment.getRole().getName())
                .toList();
        log.trace("Role for {}: {}", uid, roles);
        return roles;

    }

}
