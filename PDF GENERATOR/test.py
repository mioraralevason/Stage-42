#!/usr/bin/env python3 
import requests
from requests_oauthlib import OAuth2Session

# Replace with your actual credentials

UID="u-s4t2af-23f031abd5ab1c7afcd6b43148ddd70b2ae20692602fb8c142f94fabb55b5373"
SECRET="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"
base_url = "https://api.intra.42.fr"

# Attempt 1: Using OAuth2Session
try:
    # Create an OAuth2 session with only client_id
    client = OAuth2Session(client_id=UID)

    # Fetch the access token using client_credentials grant type
    token = client.fetch_token(
        token_url=f"{base_url}/oauth/token",
        client_id=UID,
        client_secret=SECRET,
        grant_type="client_credentials",
        include_client_id=True  # Explicitly include client_id in the request body
    )
    print("Token obtained:", token)

except ValueError as e:
    print("OAuth2Session failed:", e)
    # Fallback: Use requests directly for client_credentials
    print("Trying direct requests method...")
    response = requests.post(
        f"{base_url}/oauth/token",
        data={
            "grant_type": "client_credentials",
            "client_id": UID,
            "client_secret": SECRET
        }
    )
    if response.status_code == 200:
        token = response.json()
        print("Token obtained via requests:", token)
    else:
        print("Error fetching token:", response.status_code, response.text)
        exit(1)

# Use the token to make an authenticated request
response = client.get(f"{base_url}/v2/users", headers={"Authorization": f"Bearer {token['access_token']}"})

# Check the response
if response.status_code == 200:
    print("Data retrieved:", response.json())
else:
    print("Error:", response.status_code, response.text)