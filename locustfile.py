from locust import HttpUser, between, task

class LoginUser(HttpUser):
    wait_time = between(1, 3)
    token = None

    @task
    def login_and_fetch_roles(self):

        login_payload = {
            "login": "deman",
            "password": "Azerty123"
        }
        response = self.client.post("/oidc/login", json=login_payload)

        # Si la connexion réussit, stocke le token
        if response.status_code == 200:
            self.token = response.text
            print("Access token:", self.token)

            self.fetch_roles()
        else:
            print("Error:", response.status_code, response.text)

    def fetch_roles(self):
        if self.token:
            headers = {
                "x-authorization": self.token  # Utilise le token directement dans l'en-tête
            }
            response = self.client.get("/roles", headers=headers)

            if response.status_code == 200:
                print("Roles:", response.json())
            else:
                print("Error while fetching roles:", response.status_code, response.text)
        else:
            print("Error: empty access token")
