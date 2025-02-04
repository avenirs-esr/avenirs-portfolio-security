import os
import csv
import uuid
import random
import logging
from faker import Faker
import argparse

# Quantities
BASE_NUM_PRINCIPALS = 100
NUM_PERMISSIONS = 1000
BASE_NUM_STRUCTURES = 4
MAX_NUM_STRUCTURES=400
NUM_RESOURCE_TYPES = 150
BASE_NUM_RESOURCES = 1000
NUM_ROLES_AND_ACTIONS = 400

MAX_PRINCIPAL_ASSIGNMENTS = 100
MAX_SCOPE_RESOURCES = 10
MAX_CONTEXT_STRUCTURES = 2
MAX_ACTION_ROUTES = 4
MAX_PRINCIPAL_STRUCTURES = 10
MAX_INVOLVED_PERMISSIONS = 20

# Logs
log_directory = './logs'
os.makedirs(log_directory, exist_ok=True)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler(os.path.join(log_directory, 'faker-fixtures.log'))
console_handler = logging.StreamHandler()

formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)

logger.addHandler(file_handler)
logger.addHandler(console_handler)

class DataGenerator:
    def __init__(self, multiplicator=1):
        self.fake = Faker()
        self.multiplicator = multiplicator

    def scale(self, value):
        return int(value * self.multiplicator)

    def generate_principals(self):
        uid_start = 4000
        principals = []

        for i in range(self.scale(BASE_NUM_PRINCIPALS)):
            uid_number = uid_start + i
            gid_number = uid_start + i
            login = f"user_{i}"
            given_name = f"User{i}"
            sn = f"Lastname{i}"
            cn = f"{given_name} {sn}"
            mail = f"{login}@example.com"
            gecos = f"{given_name} {sn}"

            principal = {
                "id": str(uuid.uuid4()),
                "login": login,
                "uid": login,
                "uidNumber": uid_number,
                "gidNumber": gid_number,
                "cn": cn,
                "sn": sn,
                "givenName": given_name,
                "mail": mail,
                "gecos": gecos,
                "homeDirectory": f"/home/perso/{login}",
                "userPassword": f"{login}_pwd"
            }
            principals.append(principal)
        return principals


    def generate_roles(self):
        return [{"id": str(uuid.uuid4()), "name": f"role_{i}", "description": f"Description of role {i}"} for i in range(NUM_ROLES_AND_ACTIONS)]

    def generate_permissions(selfunt):
        return [{"id": str(uuid.uuid4()), "name": f"permission_{i}", "description": f"Description of permission {i}"} for i in range(NUM_PERMISSIONS)]

    def generate_structures(self):
        return [{"id": str(uuid.uuid4()), "name": f"structure_{i}", "description": f"Description of structure {i}"} for i in range(min(self.scale(BASE_NUM_STRUCTURES), MAX_NUM_STRUCTURES))]

    def generate_resources(self, resource_type_ids):
        return [
            {
                "id": str(uuid.uuid4()),
                "selector": f"resource_{i}",
                "id_resource_type": resource_type_ids[i % len(resource_type_ids)],
            }
            for i in range(self.scale(BASE_NUM_RESOURCES))
        ]

    # For each principal the number of created assignments is in range [O, MAX_PRINCIPAL_ASSIGNMENTS]
    def generate_assignments(self, role_ids, principal_ids):
        unique_combinations = set()
        assignments = []
        scopes = []
        contexts = []

        for id_principal in principal_ids:
            num_assignments = random.randint(0, MAX_PRINCIPAL_ASSIGNMENTS)

            for _ in range(num_assignments):
                id_role = role_ids[random.randint(0, len(role_ids) - 1)]

                # Génération d'un scope
                id_scope = str(uuid.uuid4())
                scope = {"id": id_scope, "name": f"scope_{len(scopes)}"}
                scopes.append(scope)

                # Génération d'un context
                id_context = str(uuid.uuid4())
                context = {
                    "id": id_context,
                    #"validity_start": self.fake.date_time_this_year().isoformat(),
                    #"validity_end": self.fake.date_time_this_year(after_now=True).isoformat(),
                }
                contexts.append(context)

                combination = (id_role, id_principal, id_scope, id_context)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    assignments.append({
                        "id": str(uuid.uuid4()),
                        "id_role": id_role,
                        "id_principal": id_principal,
                        "id_scope": id_scope,
                        "id_context": id_context,
                        "timestamp": self.fake.date_time_this_year().isoformat(),
                    })

        return assignments, scopes, contexts



    def generate_resource_types(self):
        return [{"id": str(uuid.uuid4()), "name": f"resource_type_{i}", "description": f"Description of resource type {i}"} for i in range(NUM_RESOURCE_TYPES)]

    def generate_scope_resources(self, scope_ids, resource_ids):
        unique_combinations = set()
        scope_resources = []

        for id_scope in scope_ids:
            num_resources = random.randint(1, MAX_SCOPE_RESOURCES)

            for _ in range(num_resources):
                id_resource = resource_ids[random.randint(0, len(resource_ids) - 1)]

                combination = (id_scope, id_resource)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    scope_resources.append({
                        "id_scope": id_scope,
                        "id_resource": id_resource
                    })

        return scope_resources

    def generate_role_and_action_permissions(self, role_ids, action_ids,  permission_ids):
        unique_combinations = set()
        action_permissions = []
        role_permissions = []

        for i in range(NUM_ROLES_AND_ACTIONS):
            id_action = action_ids[i]
            id_role = role_ids[i]
            num_permissions = random.randint(1, MAX_INVOLVED_PERMISSIONS)

            for _ in range(num_permissions):
                id_permission = permission_ids[random.randint(0, len(permission_ids) - 1)]

                combination = (id_action, id_permission)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    action_permissions.append({
                        "id_action": id_action,
                        "id_permission": id_permission
                    })
                    role_permissions.append({
                        "id_role": id_role,
                        "id_permission": id_permission
                    })

        return role_permissions,action_permissions

    def generate_context_structures(self, context_ids, structure_ids):
        unique_combinations = set()
        context_structures = []

        for id_context in context_ids:
            BASE_NUM_STRUCTURES = random.randint(1, MAX_CONTEXT_STRUCTURES)

            for _ in range(min(self.scale(BASE_NUM_STRUCTURES), MAX_NUM_STRUCTURES)):
                id_structure = structure_ids[random.randint(0, len(structure_ids) - 1)]

                combination = (id_context, id_structure)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    context_structures.append({
                        "id_context": id_context,
                        "id_structure": id_structure
                    })

        return context_structures


    def generate_actions(self):
        return [{"id": str(uuid.uuid4()), "name": f"action_{i}", "description": f"Description of action {i}"} for i in range(NUM_ROLES_AND_ACTIONS)]

    def generate_action_routes(self, action_ids):
        unique_combinations = set()
        action_routes = []

        for id_action in action_ids:
            num_routes = random.randint(1, MAX_ACTION_ROUTES)

            for _ in range(num_routes):
                uri = f"/route_{random.randint(0, len(action_ids) * 10)}"
                method = random.choice(["GET", "POST", "PUT", "DELETE"])

                combination = (uri, method)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    action_routes.append({
                        "id": str(uuid.uuid4()),
                        "id_action": id_action,
                        "uri": uri,
                        "method": method,
                        "description": f"Description of route with URI {uri} and method {method}",
                    })

        return action_routes

    def generate_principal_structures(self, principal_ids, structure_ids):
        unique_combinations = set()
        principal_structures = []

        for id_principal in principal_ids:
            num_structures = random.randint(1, MAX_PRINCIPAL_STRUCTURES)

            for _ in range(num_structures):
                id_structure = structure_ids[random.randint(0, len(structure_ids) - 1)]

                combination = (id_principal, id_structure)
                if combination not in unique_combinations:
                    unique_combinations.add(combination)
                    principal_structures.append({
                        "id_principal": id_principal,
                        "id_structure": id_structure
                    })

        return principal_structures




def ensure_directory_exists(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

def write_to_csv(directory, filename, data, fieldnames):
    ensure_directory_exists(directory)
    filepath = os.path.join(directory, filename)
    logger.info("Writing to file: %s", filepath)
    with open(filepath, mode="w", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(data)
    logger.info("File generated: %s with %s entries", filename, len(data))
def write_to_ldif(output_directory, filename, principals):
    ldif_template = """dn: uid={uid},ou=people,dc=ldap-dev,dc=avenirs-esr,dc=fr
objectClass: posixAccount
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: {cn}
uidNumber: {uidNumber}
gidNumber: {gidNumber}
homeDirectory: /home/perso/{uid}
sn: {sn}
uid: {uid}
gecos: {gecos}
givenName: {givenName}
userPassword: {userPassword}
mail: {mail}
"""

    file_path = os.path.join(output_directory, filename)
    with open(file_path, 'w') as ldif_file:
        for principal in principals:
            ldif_entry = ldif_template.format(
                uid=principal["uid"],
                cn=principal["cn"],
                uidNumber=principal["uidNumber"],
                gidNumber=principal["gidNumber"],
                sn=principal["sn"],
                gecos=principal["gecos"],
                givenName=principal["givenName"],
                userPassword=principal["userPassword"],
                mail=principal["mail"]
            )
            ldif_file.write(ldif_entry + "\n")

def generate_changelog(directory, changelog_file):
    ensure_directory_exists(directory)
    changelog_path = os.path.join(directory, changelog_file)
    logger.info("Generating Liquibase changelog: %s", changelog_path)

    table_order = [
        "principal",
        "role",
        "permission",
        "structure",
        "context",
        "resource_type",
        "resource",
        "scope",
        "principal_structure",
        "context_structure",
        "action",
        "action_route",
        "scope_resource",
        "role_permission",
        "action_permission",
        "assignment",
    ]

    with open(changelog_path, mode="w") as file:
        file.write("databaseChangeLog:\n")
        for table_name in table_order:
            csv_file = f"{table_name}.csv"
            file.write(f"  - changeSet:\n")
            file.write(f"      id: \"load-{table_name}\"\n")
            file.write(f"      author: \"faker-fixtures\"\n")
            file.write(f"      changes:\n")
            file.write(f"        - loadData:\n")
            file.write(f"            file: \"{csv_file}\"\n")
            file.write(f"            tableName: \"{table_name}\"\n")

    logger.info("Changelog generated: %s", changelog_path)

def generate_fixtures(multiplicator=1):
    data_gen = DataGenerator(multiplicator)
    output_directory = f"target/fixtures/m{multiplicator}"

    changelog_file = "fixtures-changelog.yaml"

    os.makedirs(output_directory, exist_ok=True)

    logger.info(f"---- Starting data generation with multiplicator {multiplicator} ----")
    logger.info(f"Output directory: {output_directory}")

    # Logs des tailles des données
    logger.info(f"Number of principals: {int(BASE_NUM_PRINCIPALS * multiplicator)}")
    logger.info(f"Number of permissions: {int(NUM_PERMISSIONS)}")
    logger.info(f"Number of structures: {int(min(BASE_NUM_STRUCTURES * multiplicator, MAX_NUM_STRUCTURES))}")
    logger.info(f"Number of resource types: {int(NUM_RESOURCE_TYPES )}")
    logger.info(f"Number of resources: {int(BASE_NUM_RESOURCES * multiplicator)}")
    logger.info(f"Number of roles and actions: {int(NUM_ROLES_AND_ACTIONS)}")


    logger.info("Step : principals (%s)", BASE_NUM_PRINCIPALS*multiplicator)
    principals = data_gen.generate_principals()

    logger.info("Step : roles (%s)", NUM_ROLES_AND_ACTIONS)
    roles = data_gen.generate_roles()

    logger.info("Step : permissions (%s)", NUM_PERMISSIONS)
    permissions = data_gen.generate_permissions()

    logger.info("Step : structure (%s)", BASE_NUM_STRUCTURES * multiplicator)
    structures = data_gen.generate_structures()

    logger.info("Step : resource types (%s)", NUM_RESOURCE_TYPES)
    resource_types = data_gen.generate_resource_types()

    logger.info("Step : resources (%s)", BASE_NUM_RESOURCES * multiplicator)
    resources = data_gen.generate_resources([r["id"] for r in resource_types])

    logger.info("Step : actions (%s)", NUM_ROLES_AND_ACTIONS)
    actions = data_gen.generate_actions()

    logger.info("Step : assignments (max %s per principal)", MAX_PRINCIPAL_ASSIGNMENTS)
    assignments, scopes, contexts = data_gen.generate_assignments(
        [r["id"] for r in roles],
        [p["id"] for p in principals]
    )

    logger.info("Step : scope_resources (max %s resources per scope)", MAX_SCOPE_RESOURCES)
    scope_resources = data_gen.generate_scope_resources(
        [s["id"] for s in scopes], [r["id"] for r in resources]
    )

    logger.info("Step : context_structures (max %s structures per context)", MAX_CONTEXT_STRUCTURES)
    context_structures = data_gen.generate_context_structures(
        [c["id"] for c in contexts],
        [s["id"] for s in structures]
    )

    logger.info("Step : action_routes (max %s routes per action)", MAX_ACTION_ROUTES)
    action_routes = data_gen.generate_action_routes([a["id"] for a in actions])

    logger.info("Step : principal_structures (max %s structure per principal)", MAX_PRINCIPAL_STRUCTURES)
    principal_structures = data_gen.generate_principal_structures(
        [p["id"] for p in principals],
        [s["id"] for s in structures]
    )

    logger.info("Step : role_permissions & action_permissions (max %s involved permission)", MAX_INVOLVED_PERMISSIONS)
    role_permissions, action_permissions = data_gen.generate_role_and_action_permissions(
        [r["id"] for r in roles], [a["id"] for a in actions], [p["id"] for p in permissions]
    )

    logger.info("Writing principal.csv")
    filtered_principals = [{"id": p["id"], "login": p["login"]} for p in principals]
    write_to_csv(output_directory, "principal.csv", filtered_principals, filtered_principals[0].keys())

    logger.info("Writing  principal.ldif")
    write_to_ldif(output_directory, "principal.ldif", principals)

    logger.info("Writing role.csv")
    write_to_csv(output_directory, "role.csv", roles, roles[0].keys())

    logger.info("Writing permission.csv")
    write_to_csv(output_directory, "permission.csv", permissions, permissions[0].keys())

    logger.info("Writing permission.csv")
    write_to_csv(output_directory, "structure.csv", structures, structures[0].keys())

    logger.info("Writing context.csv")
    write_to_csv(output_directory, "context.csv", contexts, contexts[0].keys())

    logger.info("Writing resource_type.csv")
    write_to_csv(output_directory, "resource_type.csv", resource_types, resource_types[0].keys())

    logger.info("Writing resource.csv")
    write_to_csv(output_directory, "resource.csv", resources, resources[0].keys())

    logger.info("Writing scope.csv")
    write_to_csv(output_directory, "scope.csv", scopes, scopes[0].keys())

    logger.info("Writing assignment.csv")
    write_to_csv(output_directory, "assignment.csv", assignments, assignments[0].keys())

    logger.info("Writing scope_resource.csv")
    write_to_csv(output_directory, "scope_resource.csv", scope_resources, scope_resources[0].keys())

    logger.info("Writing role_permission.csv")
    write_to_csv(output_directory, "role_permission.csv", role_permissions, role_permissions[0].keys())

    logger.info("Writing context_structure.csv")
    write_to_csv(output_directory, "context_structure.csv", context_structures, context_structures[0].keys())

    logger.info("Writing action.csv")
    write_to_csv(output_directory, "action.csv", actions, actions[0].keys())

    logger.info("Writing action_route.csv")
    write_to_csv(output_directory, "action_route.csv", action_routes, action_routes[0].keys())

    logger.info("Writing principal_structure.csv")
    write_to_csv(output_directory, "principal_structure.csv", principal_structures, principal_structures[0].keys())

    logger.info("Writing action_permission.csv")
    write_to_csv(output_directory, "action_permission.csv", action_permissions, action_permissions[0].keys())

    logger.info("DONE.")

    generate_changelog(output_directory, changelog_file)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate fixtures with a multiplicator.")
    parser.add_argument("--multiplicator", type=float, default=1.0, help="Multiplicator for scaling generated data.")
    args = parser.parse_args()

    generate_fixtures(args.multiplicator)

