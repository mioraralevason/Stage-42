#!/usr/bin/env python3

import requests
from dotenv import load_dotenv
import os
import time

# Load environment variables
load_dotenv()

# API credentials
UID = os.getenv("UID")
SECRET = os.getenv("SECRET")
BASE_URL = "https://api.intra.42.fr"
access_token_url = f"{BASE_URL}/oauth/token"
users_url = f"{BASE_URL}/v2/users"
locations_url = f"{BASE_URL}/v2/locations"

def get_access_token():
    """Fetch access token from 42 API."""
    data = {
        'grant_type': 'client_credentials',
        'scope': 'public projects profile tig elearning forum'
    }
    r = requests.post(access_token_url, auth=(UID, SECRET), data=data)
    r.raise_for_status()
    return r.json().get('access_token')


def get_id_users(login: str, token: str = None) -> str:
    """Get user ID by login."""
    headers = {'Authorization': f'Bearer {token or get_access_token()}', "Content-Type": "application/json"}
    r = requests.get(f"{users_url}?filter[login]={login}", headers=headers)
    r.raise_for_status()
    users = r.json()
    if not users:
        raise ValueError("Utilisateur non trouvé")
    return str(users[0]["id"])


def get_user(user_id: str, token: str = None):
    """Get user data by user ID."""
    url = f"{BASE_URL}/v2/users/{user_id}"
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()


def get_user_candidature(user_id: str, token: str = None):
    """Get candidature data by user ID."""
    url = f"{BASE_URL}/v2/users/{user_id}/user_candidature"
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def get_all_events(campus_id: str = "65", token: str = None, page_size: int = 100):
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    page = 1
    all_events = []

    while True:
        url = f"{BASE_URL}/v2/campus/{campus_id}/events?page[number]={page}&page[size]={page_size}"
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        events = response.json()

        if not events:
            break

        all_events.extend(events)
        print(f"Fetched page {page}, got {len(events)} events")
        page += 1

        # Rate limit: 8 requests per second
        time.sleep(0.125)

    print(f"Total events fetched = {len(all_events)}")
    return all_events


def get_all_locations(campus_id: str = "65", token: str = None, page_size: int = 100):
    headers = {"Authorization": f"Bearer {token or get_access_token()}"}
    page = 1
    all_locations = []

    while True:
        url = f"{locations_url}?campus_id={campus_id}&page[number]={page}&page[size]={page_size}"
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        locations = response.json()

        if not locations:
            break

        all_locations.extend(locations)
        print(f"Fetched page {page}, got {len(locations)} locations")
        page += 1

        # Rate limit: 8 requests per second
        time.sleep(0.125)

    print(f"Total locations fetched = {len(all_locations)}")
    return all_locations


if __name__ == "__main__":
    try:
        token = get_access_token()
        print("Access token:", token)

        login = "juramaha"
        user_id = get_id_users(login, token)
        print(f"User ID for login {login}: {user_id}")

        # Fetch all events
        events = get_all_events(campus_id="65", token=token)
        print("First event:", events[0]["name"] if events else "No events found")

        # Fetch all locations
        locations = get_all_locations(campus_id="65", token=token)
        print("First location:", locations[0]["name"] if locations else "No locations found")

    except Exception as e:
        print(f"Error: {str(e)}")
