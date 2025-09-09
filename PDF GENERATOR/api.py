#!/usr/bin/env python3

import requests
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

# API credentials
UID = os.getenv("UID")
SECRET = os.getenv("SECRET")
BASE_URL = "https://api.intra.42.fr"
access_token_url = "https://api.intra.42.fr/oauth/token"
users_url = "https://api.intra.42.fr/v2/users"

def get_access_token():
    """Fetch access token from 42 API."""
    data = {
        'grant_type': 'client_credentials',
        'scope': 'public projects profile tig elearning forum'
    }
    try:
        r = requests.post(access_token_url, auth=(UID, SECRET), data=data)
        r.raise_for_status()
        return r.json().get('access_token')
    except requests.RequestException as e:
        raise Exception(f"Erreur lors de l'obtention du jeton: {str(e)}")

def get_id_users(login: str, token: str = None) -> str:
    """Get user ID by login."""
    headers = {'Authorization': f'Bearer {token or get_access_token()}', "Content-Type": "application/json"}
    try:
        r = requests.get(f"{users_url}?filter[login]={login}", headers=headers)
        r.raise_for_status()
        users = r.json()
        if not users:
            raise ValueError("Utilisateur non trouvé")
        return str(users[0]["id"])
    except requests.RequestException as e:
        raise Exception(f"Erreur API lors de la récupération de l'ID utilisateur: {str(e)}")
    except ValueError as e:
        raise Exception(f"Login invalide: {str(e)}")

def get_user(user_id: str, token: str = None):
    """Get user data by user ID."""
    url = f"{BASE_URL}/v2/users/{user_id}"
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        raise Exception(f"Erreur lors de la récupération de l'utilisateur: {str(e)}")

def get_user_candidature(user_id: str, token: str = None):
    """Get candidature data by user ID."""
    url = f"{BASE_URL}/v2/users/{user_id}/user_candidature"
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        raise Exception(f"Erreur lors de la récupération des données de candidature: {str(e)}")

if __name__ == "__main__":
    try:
        token = get_access_token()
        print("Access token:", token)
        login = "juramaha"
        user_id = get_id_users(login)
        print(f"User ID for login {login}: {user_id}")
        # print("User data:", get_user(user_id))
        # print("Candidature data:", get_user_candidature(user_id))
    except Exception as e:
        print(f"Error: {str(e)}")