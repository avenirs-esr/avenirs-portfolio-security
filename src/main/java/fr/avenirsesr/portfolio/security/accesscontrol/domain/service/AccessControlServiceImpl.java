package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermissionGroup;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlInvalidDateException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.AccessControlService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.*;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AccessControlServiceImpl implements AccessControlService {

  private static final String DIRECT_ROLE_PREFIX = "DIRECT_";

  private final RBACActionRepository actionRepository;
  private final RBACAssignmentRepository assignmentRepository;
  private final PrincipalRepository principalRepository;
  private final RBACPermissionRepository permissionRepository;
  private final RBACResourceRepository resourceRepository;
  private final RBACRoleRepository roleRepository;
  private final StructureRepository structureRepository;
  private final String dateFormat;

  private final Map<UUID, List<RBACPermission>> permissionsByActionId = new HashMap<>();

  @Override
  public AccessControlGrantResult grantAccess(AccessControlGrantCommand command) {
    log.trace("grantAccess, command: {}", command);

    RBACRole role =
        roleRepository
            .findById(command.roleId())
            .orElseThrow(() -> AccessControlNotFoundException.role(command.roleId()));

    Principal principal =
        principalRepository
            .findByLogin(command.login())
            .orElseThrow(() -> AccessControlNotFoundException.principal(command.login()));

    List<RBACResource> resources = resolveResources(command.resourceIds());
    List<Structure> structures = resolveStructures(command.structureIds());

    RBACContext context =
        new RBACContext(
            null,
            parseDate(command.validityStart()),
            parseDate(command.validityEnd()),
            new HashSet<>(structures));

    RBACAssignment assignment =
        new RBACAssignment(null, principal, role, new RBACScope(null, resources), context);

    RBACAssignment savedAssignment = assignmentRepository.save(assignment);

    return new AccessControlGrantResult(command.login(), true, savedAssignment.id(), null);
  }

  @Override
  public AccessControlGrantResult grantPermissions(AccessControlGrantPermissionsCommand command) {
    log.trace("grantPermissions, command: {}", command);

    Set<EPermission> requested =
        resolvePermissions(command.permissions(), command.permissionGroups());

    Principal principal =
        principalRepository
            .findByLogin(command.login())
            .orElseThrow(() -> AccessControlNotFoundException.principal(command.login()));

    Set<RBACPermission> permissions = new HashSet<>();
    for (EPermission permission : requested) {
      permissions.add(
          permissionRepository
              .findByName(permission.authority())
              .orElseThrow(
                  () ->
                      AccessControlNotFoundException.permissions(List.of(permission.authority()))));
    }

    List<RBACResource> resources = resolveResources(command.resourceIds());
    List<Structure> structures = resolveStructures(command.structureIds());

    RBACContext context =
        new RBACContext(
            null,
            parseDate(command.validityStart()),
            parseDate(command.validityEnd()),
            new HashSet<>(structures));

    RBACRole directRole =
        roleRepository.save(
            new RBACRole(
                null,
                DIRECT_ROLE_PREFIX + UUID.randomUUID(),
                "Direct permissions assignment",
                permissions));

    RBACAssignment savedAssignment =
        assignmentRepository.save(
            new RBACAssignment(
                null, principal, directRole, new RBACScope(null, resources), context));

    return new AccessControlGrantResult(command.login(), true, savedAssignment.id(), null);
  }

  @Override
  public AccessControlRevokeResult revokeAccess(AccessControlRevokeCommand command) {
    log.trace("revokeAccess, command: {}", command);

    assignmentRepository.deleteById(command.assignmentId());

    return new AccessControlRevokeResult(command.login(), true, command.assignmentId(), null);
  }

  @Override
  public boolean isAuthorized(String login, UUID actionId, UUID resourceId) {
    log.trace("isAuthorized, login: {}, actionId: {}, resourceId: {}", login, actionId, resourceId);

    Principal principal = principalRepository.findByLogin(login).orElse(null);

    if (principal == null) {
      log.debug("isAuthorized, principal not found for login: {}", login);
      return false;
    }

    RBACContext executionContext = createExecutionContext(principal);

    List<RBACAssignment> principalAssignments =
        assignmentRepository.findByPrincipalContextAndResource(login, executionContext, resourceId);

    HashSet<RBACPermission> principalPermissions =
        principalAssignments.stream()
            .map(assignment -> assignment.role().permissions())
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(HashSet::new));

    if (principalPermissions.isEmpty()) {
      log.debug("isAuthorized, principal {} has no permission", login);
      return false;
    }

    List<RBACPermission> requiredPermissions = fetchPermissions(actionId);

    boolean accessGranted =
        requiredPermissions != null && principalPermissions.containsAll(requiredPermissions);

    log.trace("isAuthorized, accessGranted: {}", accessGranted);

    return accessGranted;
  }

  private RBACContext createExecutionContext(Principal principal) {
    return new RBACContext(null, null, null, new HashSet<>(principal.getStructures()));
  }

  private Set<EPermission> resolvePermissions(List<String> names, List<String> groupNames) {
    Set<EPermission> result = EnumSet.noneOf(EPermission.class);
    List<String> unknownPermissions = new ArrayList<>();
    List<String> unknownGroups = new ArrayList<>();

    for (String name : names == null ? List.<String>of() : names) {
      Arrays.stream(EPermission.values())
          .filter(permission -> permission.authority().equals(name))
          .findFirst()
          .ifPresentOrElse(result::add, () -> unknownPermissions.add(name));
    }

    for (String name : groupNames == null ? List.<String>of() : groupNames) {
      Arrays.stream(EPermissionGroup.values())
          .filter(group -> group.name().equals(name))
          .findFirst()
          .ifPresentOrElse(
              group -> result.addAll(group.permissions()), () -> unknownGroups.add(name));
    }

    if (!unknownPermissions.isEmpty()) {
      throw AccessControlNotFoundException.permissions(unknownPermissions);
    }
    if (!unknownGroups.isEmpty()) {
      throw AccessControlNotFoundException.permissionGroups(unknownGroups);
    }
    if (result.isEmpty()) {
      throw new IllegalArgumentException("At least one permission or permission group is required");
    }

    return result;
  }

  private List<RBACResource> resolveResources(List<UUID> resourceIds) {
    if (resourceIds == null || resourceIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<RBACResource> resources = resourceRepository.findAllByIds(resourceIds);
    List<UUID> foundIds = resources.stream().map(RBACResource::id).toList();

    List<UUID> missingIds = resourceIds.stream().filter(id -> !foundIds.contains(id)).toList();

    if (!missingIds.isEmpty()) {
      log.warn("Missing resource ids: {}", missingIds);
      throw AccessControlNotFoundException.resources(missingIds);
    }

    return resources;
  }

  private List<Structure> resolveStructures(List<UUID> structureIds) {
    if (structureIds == null || structureIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<Structure> structures = structureRepository.findAllByIds(structureIds);
    List<UUID> foundIds = structures.stream().map(Structure::id).toList();

    List<UUID> missingIds = structureIds.stream().filter(id -> !foundIds.contains(id)).toList();

    if (!missingIds.isEmpty()) {
      log.warn("Missing structure ids: {}", missingIds);
      throw AccessControlNotFoundException.structures(missingIds);
    }

    return structures;
  }

  private LocalDateTime parseDate(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat)).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new AccessControlInvalidDateException(dateFormat, value, e);
    }
  }

  private List<RBACPermission> fetchPermissions(UUID actionId) {
    if (actionId != null && !permissionsByActionId.containsKey(actionId)) {
      List<RBACPermission> permissions =
          actionRepository.findById(actionId).map(RBACAction::permissions).orElse(null);

      permissionsByActionId.put(actionId, permissions);
    }

    return permissionsByActionId.get(actionId);
  }
}
