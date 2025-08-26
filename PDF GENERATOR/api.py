#!/usr/bin/env python3

import requests
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

# API credentials
UID = os.getenv("UID")  # Your client_id
SECRET = os.getenv("SECRET")  # Your client_secret
BASE_URL = "https://api.intra.42.fr"

access_token_url = "https://api.intra.42.fr/oauth/token"
users_url = "https://api.intra.42.fr/v2/users"

########################################## - **$** - ##########################################
cursus_id = [80]
# les cursus_ids : 
# [79] pour "Discovery Piscine - AI Fundamentals for All"
# [80] pour "Discovery Piscine - Core Python Programming"
# [68] pour "Discovery Piscine - Cybersecurity"
# [69] pour "Discovery Piscine - Python"
# [3]  pour "Discovery Piscine - Web Programming Essentials"
# [76] pour "Remote Discovery - Web"
########################################## - **$** - ##########################################

def get_access_token():
    access_token = None
    data = {
        'grant_type': 'client_credentials',
        'scope': 'public projects profile tig elearning forum'
    }
    try:
        r = requests.post(access_token_url, auth=(UID, SECRET), data=data)
        r.raise_for_status()
        access_token = r.json().get('access_token')
    except requests.RequestException as e:
        print(f"Erreur lors de l'obtention du jeton : {e}")
    return access_token

########################################## - **$** - ##########################################
# on récupère les user_id à partir des logins
########################################## - **$** - ##########################################
def get_id_users(login):
    headers = { 'Authorization': 'Bearer ' + get_access_token(), "Content-Type": "application/json" }
    r = requests.get(users_url + "?filter[login]=" + login, headers=headers)
    return str(r.json()[0]["id"])

def get_user(user_id, token):
    """Récupère les infos d'un utilisateur"""
    url = f"{BASE_URL}/v2/users/{user_id}"
    headers = {"Authorization": f"Bearer {token}"}
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        print(f"Error retrieving user: {e}")
        print(f"Status code: {response.status_code}")
        print(f"Response content: {response.text}")
        raise

def get_user_candidature(user_id, token):
    """Retrieve user candidature data"""
    url = f"{BASE_URL}/v2/users/{user_id}/user_candidature"
    headers = {"Authorization": f"Bearer {token}"}
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        print(f"Error retrieving candidature (user_candidature): {e}")
        print(f"Status code: {response.status_code}")
        print(f"Response content: {response.text}")
        raise

# Test the endpoints
if __name__ == "__main__":
    user_id = "238787"  # The user ID you tested
    # Use the provided token or fetch a new one
    token = get_access_token()
    print("Access token:", token)
    login = "mralevas"

    try:
        print("Testing get_id_users function:")
        print(get_id_users(login))
    except Exception as e:
        print(f"Error occurred (get_id_users): {e}")

    try:
        print("Testing /users/{id} endpoint:")
        user_data = get_user(user_id, token)
        print("User data:", user_data)
    except Exception as e:
        print(f"Error occurred (user): {e}")

    try:
        print("\nTesting /user_candidature endpoint:")
        candidature_data = get_user_candidature(user_id, token)
        print("Candidature data:", candidature_data)
    except Exception as e:
        print(f"Error occurred (user_candidature): {e}")