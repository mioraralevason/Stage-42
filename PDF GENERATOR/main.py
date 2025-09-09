#!/usr/bin/env python3

import os
import asyncio
import aiohttp
import math
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# API credentials
UID = os.getenv("UID")
SECRET = os.getenv("SECRET")
BASE_URL = "https://api.intra.42.fr"
access_token_url = f"{BASE_URL}/oauth/token"
users_url = f"{BASE_URL}/v2/users"
locations_url = f"{BASE_URL}/v2/locations"

MAX_REQ_PER_SECOND = 8  # rate limit
sem = asyncio.Semaphore(MAX_REQ_PER_SECOND)


def get_access_token():
    """Fetch access token synchronously (for simplicity)."""
    import requests
    data = {'grant_type': 'client_credentials', 'scope': 'public projects profile tig elearning forum'}
    r = requests.post(access_token_url, auth=(UID, SECRET), data=data)
    r.raise_for_status()
    return r.json().get("access_token")


def get_id_users(login: str, token: str) -> str:
    import requests
    headers = {'Authorization': f'Bearer {token}', "Content-Type": "application/json"}
    r = requests.get(f"{users_url}?filter[login]={login}", headers=headers)
    r.raise_for_status()
    users = r.json()
    if not users:
        raise ValueError("Utilisateur non trouvé")
    return str(users[0]["id"])


async def fetch_page(session, url):
    async with sem:
        async with session.get(url) as response:
            data = await response.json()
            await asyncio.sleep(1 / MAX_REQ_PER_SECOND)
            return data


async def fetch_all_pages(url_template, total_pages, token):
    all_data = []
    async with aiohttp.ClientSession(headers={"Authorization": f"Bearer {token}"}) as session:
        tasks = [fetch_page(session, url_template.format(page=page)) for page in range(1, total_pages + 1)]
        pages = await asyncio.gather(*tasks)
        for page_data in pages:
            all_data.extend(page_data)
    return all_data


async def get_all_events(token, campus_id="65", page_size=100):
    # Première requête pour connaître le nombre total d'événements
    async with aiohttp.ClientSession(headers={"Authorization": f"Bearer {token}"}) as session:
        url = f"{BASE_URL}/v2/campus/{campus_id}/events?page[number]=1&page[size]={page_size}"
        async with session.get(url) as resp:
            first_page = await resp.json()
            total_events = int(resp.headers.get("X-Total", "1000000"))
            total_pages = math.ceil(total_events / page_size)

    url_template = f"{BASE_URL}/v2/campus/{campus_id}/events?page[number]={{page}}&page[size]={page_size}"
    all_events = await fetch_all_pages(url_template, total_pages, token)
    print(f"Total events fetched = {len(all_events)}")
    return all_events


async def get_all_locations(token, campus_id="65", page_size=100):
    async with aiohttp.ClientSession(headers={"Authorization": f"Bearer {token}"}) as session:
        url = f"{locations_url}?campus_id={campus_id}&page[number]=1&page[size]={page_size}"
        async with session.get(url) as resp:
            first_page = await resp.json()
            total_locations = int(resp.headers.get("X-Total", "1000000"))
            total_pages = math.ceil(total_locations / page_size)

    url_template = f"{locations_url}?campus_id={campus_id}&page[number]={{page}}&page[size]={page_size}"
    all_locations = await fetch_all_pages(url_template, total_pages, token)
    print(f"Total locations fetched = {len(all_locations)}")
    return all_locations


async def main():
    # récupère le token etc.
    token = get_access_token()
    
    # crée les tâches
    tasks = [
        get_all_events(campus_id="65", token=token),
        get_all_locations(campus_id="65", token=token)
    ]
    
    all_data = await asyncio.gather(*tasks)
    events, locations = all_data
    
    print("First event:", events[0]["name"] if events else "No events found")
    print("First location:", locations[0]["name"] if locations else "No locations found")

# lance la boucle
asyncio.run(main())