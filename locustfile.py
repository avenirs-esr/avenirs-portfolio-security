from locust import HttpUser, between, task
import sys
from datetime import datetime, timedelta
import os
import logging
import random
import csv


# Logging setup. Don't forget the option --skip-log-setup to avoid specific locust settings.
LOG_DIRECTORY = './logs'
os.makedirs(LOG_DIRECTORY, exist_ok=True)

FIXTURES_DIRECTORY = './target/fixtures'
PRINCIPAL_CSV_FILE = os.path.join(FIXTURES_DIRECTORY, 'principal.csv')
RESOURCE_CSV_FILE = os.path.join(FIXTURES_DIRECTORY, 'resource.csv')
ACTION_ROUTE_CSV_FILE = os.path.join(FIXTURES_DIRECTORY, 'action_route.csv')

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler(os.path.join(LOG_DIRECTORY, 'avenirs-portfolio-security.load-test.log'))
console_handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)
logger.addHandler(file_handler)
logger.addHandler(console_handler)


PRINCIPAL_LOGINS = []
with open(PRINCIPAL_CSV_FILE, newline='') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        PRINCIPAL_LOGINS.append({"login": row["login"]})

RESOURCE_IDS = []
with open(RESOURCE_CSV_FILE, newline='') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        RESOURCE_IDS.append({"id": row["id"]})

ROUTES = []
with open(ACTION_ROUTE_CSV_FILE, newline='') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        ROUTES.append({"uri": row["uri"], "method": row["method"]})


class AvenirsPortfolioSecurityLoadTest(HttpUser):
    wait_time = between(1, 3)
    token = None

    @task
    def run_load_test(self):

        user = random.choice(PRINCIPAL_LOGINS)
        login_payload = {
            "login": user["login"],
            "password": f"{user["login"]}_pwd"
        }
        response = self.client.post("/oidc/login", json=login_payload)

        if response.status_code == 200:
            self.token = response.text
            logger.debug("Access for user %s, token: %s", "null" if not self.token else f"{self.token[:4]}(...)", login_payload)
            self.fetch_roles()
            resources = random.sample(RESOURCE_IDS, random.randint(5, 20))
            logger.debug("len(ROUTES): %s", len(ROUTES))
            route = ROUTES[random.randint(0, len(ROUTES) - 1)]
            logger.debug("Route: %s", route)
            for resource in resources:
                self.is_authorized(route["uri"], route["method"], user["login"], resource["id"])
        else:
            logger.error("Error during authentication for user '%s', status code: %s, response: %s",
                         user['login'], response.status_code, response.text)

    def fetch_roles(self):
        if self.token:
            headers = {"x-authorization": self.token}
            response = self.client.get("/roles", headers=headers)

            if response.status_code == 200:
                logger.debug("get /roles: %s roles fetched", len(response.json()))
            else:
                logger.error("Error while fetching roles, status code: %s, response: %s", response.status_code, response.text)
        else:
            logger.error("Error: No access token")


    def is_authorized(self,  uri, method, login, resource_id):
            if self.token:
                headers = {
                    "x-authorization": self.token
                }
                params = {
                    "uri": uri,
                    "method": method,
                    "resourceId": resource_id
                }

                with self.client.get(
                        "/access-control/authorize",
                        params=params,
                        headers=headers,
                        catch_response=True,
                        name="/access-control/authorize") as response:
                    logger.debug("Authorization for login %s, uri %s, method %s and resource %s returned: %s .", login, uri, method, resource_id, response.status_code )
                    response.success()
            else:
                logger.error("Error: empty access token")