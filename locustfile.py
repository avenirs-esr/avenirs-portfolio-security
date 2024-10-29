from locust import HttpUser, between, task

class LoginUser(HttpUser):
    wait_time = between(1, 3)  # Temps d'attente entre les tâches
    token = None  # Variable pour stocker le token d'accès

    @task
    def login_and_fetch_roles(self):
        # Login pour récupérer le token
        login_payload = {
            "login": "deman",       # Remplacez par le login
            "password": "Azerty123"  # Remplacez par le mot de passe
        }
        response = self.client.post("/oidc/login", json=login_payload)

        # Si la connexion réussit, stocke le token
        if response.status_code == 200:
            self.token = response.text
            print("Token d'accès obtenu:", self.token)
            # Ensuite, appelle le endpoint /roles avec le token
            self.fetch_roles()
        else:
            print("Erreur de connexion:", response.status_code, response.text)

    def fetch_roles(self):
        # Vérifie que le token est disponible avant d'effectuer la requête
        if self.token:
            headers = {
                "x-authorization": self.token  # Utilise le token directement dans l'en-tête
            }
            response = self.client.get("/roles", headers=headers)

            # Affiche la réponse du endpoint /roles
            if response.status_code == 200:
                print("Rôles:", response.json())
            else:
                print("Erreur lors de la récupération des rôles:", response.status_code, response.text)
        else:
            print("Token non disponible pour accéder au endpoint /roles")
