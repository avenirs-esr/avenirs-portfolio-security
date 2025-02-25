from locust import HttpUser, between, task, events
import sys
from datetime import datetime, timedelta
import requests
import os
import logging
import random
import csv

@events.init_command_line_parser.add_listener
def add_custom_arguments(parser):
    parser.add_argument('--fixtures-directory', type=str, default='./target/fixtures')



FIXTURES_DIRECTORY = './target/fixtures'
PRINCIPAL_LOGINS=[]
RESOURCE_IDS = []
ROUTES = []



def load_fixtures():
    fixtures_directory = sys.argv[sys.argv.index('--fixtures-directory') + 1] if '--fixtures-directory' in sys.argv else './target/fixtures'
    principal_csv_file = os.path.join(fixtures_directory, 'principal.csv')
    resource_csv_file = os.path.join(fixtures_directory, 'resource.csv')
    action_route_csv_file = os.path.join(fixtures_directory, 'action_route.csv')

    with open(principal_csv_file, newline='') as csvfile:
        print(f"final PRINCIPAL_CSV_FILE = {principal_csv_file}")
        reader = csv.DictReader(csvfile)
        for row in reader:
            PRINCIPAL_LOGINS.append({"login": row["login"]})

    if not PRINCIPAL_LOGINS:
        print("Error: No data loaded in PRINCIPAL_LOGINS")
        sys.exit(1)

    with open(resource_csv_file, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            RESOURCE_IDS.append({"id": row["id"]})

    if not RESOURCE_IDS:
        print("Error: No data loaded in RESOURCE_IDS")
        sys.exit(1)

    with open(action_route_csv_file, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            ROUTES.append({"uri": row["uri"], "method": row["method"]})

    if not ROUTES:
        print("Error: No data loaded in ROUTES")
        sys.exit(1)


load_fixtures()
# Logging setup. Don't forget the option --skip-log-setup to avoid specific locust settings.
LOG_DIRECTORY = './logs'
os.makedirs(LOG_DIRECTORY, exist_ok=True)

#print(f"PRINCIPAL_CSV_FILE = {PRINCIPAL_CSV_FILE}")
#sys.exit(0)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler(os.path.join(LOG_DIRECTORY, 'avenirs-portfolio-security.load-test.log'))
console_handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)
logger.addHandler(file_handler)
logger.addHandler(console_handler)


class AvenirsPortfolioSecurityLoadTest(HttpUser):
    wait_time = between(1, 3)
    token = None

    @task
    def run_load_test(self):
        #auth_host = "http://localhost:12000"
        #auth_host = "http://localhost/avenirs-portfolio-security"
        auth_host = "http://srv-dev-avenir.srv-avenir.brgm.recia.net/avenirs-portfolio-security"
        user = random.choice(PRINCIPAL_LOGINS)
        login_payload = {
            "login": user["login"],
            "password": f"{user["login"]}_pwd"
        }
        response =  requests.post(f"{auth_host}/oidc/login", json=login_payload)

        if response.status_code == 200:
            self.token = response.text
            logger.debug("Access for user %s, token: %s", "null" if not self.token else f"{self.token[:4]}(...)", login_payload)
            resource = RESOURCE_IDS[0]
            logger.debug("len(ROUTES): %s", len(ROUTES))
            route = ROUTES[0]
            logger.debug("Route: %s", route)
            self.ac_integration_test(user["login"], resource["id"])
        else:
            logger.error("Error during authentication for user '%s', status code: %s, response: %s",
                         user['login'], response.status_code, response.text)



    def ac_integration_test(self, login, resource_id):
            if self.token:
                headers = {
                    "x-authorization": self.token
                }
                params = {
                    "resourceId": resource_id
                }

                with self.client.get(
                        "/ac-inlined",
                        params=params,
                        headers=headers,
                        catch_response=True,
                        #name="/node-api/ac-inlined") as response:
                    name="/apisix-gw/ac-in-apim") as response:
                    logger.debug("Result for login %s and resource %s returned: %s .", login, resource_id, response.status_code )
                    response.success()
            else:
                logger.error("Error: empty access token")