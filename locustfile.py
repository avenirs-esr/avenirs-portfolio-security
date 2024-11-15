from locust import HttpUser, between, task
from datetime import datetime, timedelta
import os
import logging
# import json

log_directory = './logs'
os.makedirs(log_directory, exist_ok=True)

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler(os.path.join(log_directory, 'avenirs-portfolio-security.load-test.log'))
console_handler = logging.StreamHandler()

#file_handler.setLevel(logging.DEBUG)
#console_handler.setLevel(logging.DEBUG)

formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)

logger.addHandler(file_handler)
logger.addHandler(console_handler)

class AvenirsPortfolioSecurityLoadTest(HttpUser):
    wait_time = between(1, 3)
    token = None


    @task
    def login_and_fetch_roles(self):

        self.executed_once = True
        login_payload = {
            "login": "deman",
            "password": "Azerty123"
        }
        response = self.client.post("/oidc/login", json=login_payload)
        print("!!!!!!!!!!!!! DEBUT")
        logger.debug("debug message")
        logger.info("info message")
        print("!!!!!!!!!!!!! FIN")

        if response.status_code == 200:
            self.token = response.text
            logger.debug("Access token: %s",  "null" if not self.token else f"{self.token[:4]}(...)")
            logger.info("Access token: %s",  "null" if not self.token else f"{self.token[:4]}(...)")

            grant_response = self.grant_access()
            self.fetch_roles()

            # self.is_authorized(200)

            if grant_response and grant_response.status_code == 200:
                self.revoke_access(grant_response.json())

            # self.fetch_roles()
            # self.is_authorized(403)
        else:
            print("Error:", response.status_code, response.text)

    def fetch_roles(self):
        if self.token:
            headers = {
                "x-authorization": self.token
            }
            response = self.client.get("/roles", headers=headers)

            if response.status_code == 200:
                # print("Roles:", response.json())
                print("get /roles: OK")
                # print("Roles:", json.dumps(response.json(), indent=4))
            else:
                print ("!!!! get /roles error !!!!")
                print("Error while fetching roles:", response.status_code, response.text)
        else:
            print("Error: No access token")

    def grant_access(self):
        if self.token:
            headers = {
                "x-authorization": self.token
            }
            current_date = datetime.now()
            validity_start = (current_date - timedelta(days=30)).strftime("%Y/%m/%d")
            validity_end = (current_date + timedelta(days=30)).strftime("%Y/%m/%d")
            grant_payload = {
                "login": "deman",
                "roleId": "00000000-0000-0000-0000-000000000002", # Owner
                "resourceIds": ["00000000-0000-0000-0000-000000000001"],
                 "validityStart": validity_start,
                 "validityEnd": validity_end,
                 "structureIds": ["00000000-0000-0000-0000-000000000002"]
            }
            response = self.client.post("/access-control/grant", json=grant_payload, headers=headers)

            if response.status_code == 200:
                print("FILE Access granted:", response.json())
            else:
                print("Error while granting access:", response.status_code, response.text)

            return  response
        else:
            print("Error: empty access token")
            return None


    def revoke_access(self, grant_response):

        if self.token:
            headers = {
                "x-authorization": self.token
            }
            revoke_payload = {
                "login": grant_response["login"],
                "roleId": grant_response["assignmentId"]["role"],
                "scopeId": grant_response["assignmentId"]["scope"],
                "contextId": grant_response["assignmentId"]["context"]
            }
            print("FILE Revoking access, contextId: ", grant_response["assignmentId"]["context"], "scopeId",  grant_response["assignmentId"]["scope"])
            response = self.client.delete("/access-control/grant", json=revoke_payload, headers=headers)

            if response.status_code == 200:
                print("Access revoked:", response.json())
            else:
                print("Error while revoking access:", response.status_code, response.text)
        else:
            print("Error: empty access token")

    def is_authorized(self, expected_status=200):
        if self.token:
            headers = {
                "x-authorization": self.token
            }
            params = {
                "uri": "/share/read",
                "method": "POST",
                "resourceId": "00000000-0000-0000-0000-000000000001"
            }

            with self.client.get("/access-control/authorize", params=params, headers=headers, catch_response=True) as response:
                if response.status_code == expected_status:
                    print(f"Authorization returned {expected_status} as expected.")
                else:
                    print("Unexpected status:", response.status_code, response.text)
                response.success()
        else:
            print("Error: empty access token")