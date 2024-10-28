package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecificationHelper;
import fr.avenirsesr.portfolio.security.services.RBACAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * <h1>RoleController</h1>
 * <p>
 * <b>Description:</b> used to retrieve the roles associated to a principal.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 28/10/2024
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
public class RoleController {


    /**
     * Assignment service, used to retrieve roles assigned to a principal for instance.
     */
    @Autowired
    private RBACAssignmentService assignmentService;

    /**
     * Gives the roles associated to a users.
     * @return The list of role names.
     */
    @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
    @GetMapping("${avenirs.access.control.roles}")
    public List<String> getRoles() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

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
