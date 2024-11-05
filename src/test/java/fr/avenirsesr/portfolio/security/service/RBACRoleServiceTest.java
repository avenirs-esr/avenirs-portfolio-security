package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.model.RBACPermission;
import fr.avenirsesr.portfolio.security.model.RBACRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACRoleServiceTest {

    @Value("${avenirs.test.rbac.role.service.role.id}")
    private UUID roleId;

    @Value("${avenirs.test.rbac.role.service.role.name}")
    private String roleName;

    @Value("${avenirs.test.rbac.role.service.role.description}")
    private String roleDescription;

    @Value("${avenirs.test.rbac.role.service.new.role.name}")
    private String newRoleName;

    @Value("${avenirs.test.rbac.role.service.new.role.description}")
    private String newRoleDescription;

    @Value("${avenirs.test.rbac.role.service.new.role.permission.names}")
    private String[] newRolePermissionNames;

    @Value("${avenirs.test.rbac.role.service.all.role.names}")
    private String[] allRoleNames;



    @Autowired
    private RBACRoleService roleService;

    @Test
    void getRoleById() {

        RBACRole role = roleService.getRoleById(roleId)
                .orElseThrow(() -> new AssertionError("Role not found with ID: " + roleId));

        assertEquals(roleName, role.getName());
        assertEquals(roleDescription, role.getDescription());

        Optional<RBACRole> response = roleService.getRoleById(UUID.fromString("00000000-0000-0000-0000-000000000100"));
        assertTrue(response.isEmpty());
    }

    @Test
    void getRoleByName() {

        RBACRole role = roleService.getRoleByName(roleName)
                .orElseThrow(() -> new AssertionError("Role not found with Name: " + roleName));

        assertEquals(roleId, role.getId());
        assertEquals(roleName, role.getName());
        assertEquals(roleDescription, role.getDescription());

        Optional<RBACRole> response = roleService.getRoleByName(newRoleName);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllRoles() {
        List<RBACRole> actual  = roleService.getAllRoles();
        assertThat(actual).hasSize(allRoleNames.length);
        assertThat(actual.stream().map(RBACRole::getName)).containsExactlyInAnyOrder(allRoleNames);
    }

    @Test
    void createRole() {
        RBACRole newRole = new RBACRole()
                .setName(newRoleName)
                .setDescription(newRoleDescription)
                .setPermissions(Arrays.stream(newRolePermissionNames)
                        .map(name -> new RBACPermission()
                                    .setName(name)
                                    .setDescription(""))
                        .collect(Collectors.toList()));

        RBACRole savedRole = roleService.createRole(newRole);
        assertNotNull(savedRole);

        RBACRole fetchedRole = roleService.getRoleById(savedRole.getId())
                .orElseThrow(() -> new AssertionError("Role not found with ID: " + savedRole.getId()));

        assertEquals(newRoleName, fetchedRole.getName(), "new Role name");
        assertEquals(newRoleDescription, fetchedRole.getDescription(), "new Role description");

        assertThat(fetchedRole.getPermissions())
                .as("New role permission")
                .extracting(RBACPermission::getName)
                .containsExactlyInAnyOrder(newRolePermissionNames);

    }

    @Test
    void updateRole() {
        RBACRole newRole = new RBACRole()
                .setName(newRoleName)
                .setDescription(newRoleDescription)
                .setPermissions(Arrays.stream(newRolePermissionNames)
                        .map(name -> new RBACPermission()
                                .setName(name)
                                .setDescription(""))
                        .collect(Collectors.toList()));

        RBACRole savedRole = roleService.createRole(newRole);
        assertNotNull(savedRole);

        assertEquals(newRoleName, savedRole.getName(), "Update Role initial name");
        String updatedName = newRoleName + "Updated";

        RBACRole updateRole = new RBACRole()
                .setId(newRole.getId())
                .setName(updatedName)
                .setDescription(newRoleDescription)
                .setPermissions(newRole.getPermissions());


         roleService.updateRole(updateRole);

        RBACRole fetchedRole = roleService.getRoleById(savedRole.getId())
                .orElseThrow(() -> new AssertionError("Role not found with ID: " + savedRole.getId()));
        assertEquals(updatedName, fetchedRole.getName(),"Updated role name");
  }

    @Test
    void deleteRole() {

        RBACRole newRole = new RBACRole()
                .setName(newRoleName)
                .setDescription(newRoleDescription)
                .setPermissions(Arrays.stream(newRolePermissionNames)
                        .map(name -> new RBACPermission()
                                .setName(name)
                                .setDescription(""))
                        .collect(Collectors.toList()));

        RBACRole savedRole = roleService.createRole(newRole);

        assertNotNull(savedRole);

        Optional<RBACRole> fetchedRole = roleService.getRoleById(savedRole.getId());
        assertTrue(fetchedRole.isPresent(), "Delete Role, present before delete");

        roleService.deleteRole(savedRole.getId());

        fetchedRole = roleService.getRoleById(savedRole.getId());
        assertFalse(fetchedRole.isPresent(), "Delete role, role deleted");
    }
}