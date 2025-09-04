# Installation
# pip install flask requests google-generativeai

from flask import Flask, request
import requests
import google.generativeai as genai
import os
import urllib.parse

app = Flask(__name__)

# Clé Gemini
genai.configure(api_key="AIzaSyAFDRyfPUkeCvWrUA21Dh1kibkEQQN_r4g")

# Page Access Token et Verification Token de Messenger
PAGE_ACCESS_TOKEN = "YOUR_PAGE_ACCESS_TOKEN"
VERIFY_TOKEN = "YOUR_VERIFY_TOKEN"

# Charger le contexte
categories_files = ["inscription.txt","international.txt","admission.txt","formation.txt","piscine.txt","rentree.txt"]
context_text = ""
for file in categories_files:
    if os.path.exists(file):
        with open(file, "r", encoding="utf-8") as f:
            context_text += f"Contenu de {file} :\n" + f.read() + "\n\n"

# Vérification webhook
@app.route("/webhook", methods=["GET"])
def verify():
    if request.args.get("hub.mode") == "subscribe" and request.args.get("hub.verify_token") == VERIFY_TOKEN:
        return request.args.get("hub.challenge")
    return "Token invalide", 403

# Recevoir messages
@app.route("/webhook", methods=["POST"])
def webhook():
    data = request.get_json()
    for entry in data.get("entry", []):
        for messaging_event in entry.get("messaging", []):
            sender_id = messaging_event["sender"]["id"]
            if "message" in messaging_event and "text" in messaging_event["message"]:
                user_message = messaging_event["message"]["text"]

                # Encoder la question pour Google si nécessaire
                encoded_question = urllib.parse.quote_plus(user_message)
                google_link = f"https://googlethatforyou.com/?q={encoded_question}"

                # Prompt Gemini avec fallback sur Google
                system_instruction = (
                    "Tu es un assistant spécialisé dans l'école 42 Antananarivo. "
                    "Répond de manière naturelle et claire. "
                    "Tu dois répondre uniquement en utilisant les informations fournies ci-dessous. "
                    f"Si la question n’est pas liée à ces informations ou si tu n’y trouves pas la réponse, "
                    f"réponds uniquement : 'Je ne peux pas répondre à cette question. Vas sur ce lien : {google_link}'\n\n"
                    "Contexte :\n" + context_text
                )

                # Générer la réponse
                response = genai.GenerativeModel("gemini-1.5-flash").generate_content(
                    system_instruction + "\n\nQuestion : " + user_message
                )

                # Envoyer la réponse à Messenger
                reply_text = response.text
                send_message(sender_id, reply_text)
    return "ok", 200

def send_message(recipient_id, message_text):
    url = f"https://graph.facebook.com/v17.0/me/messages?access_token={PAGE_ACCESS_TOKEN}"
    payload = {
        "recipient": {"id": recipient_id},
        "message": {"text": message_text}
    }
    requests.post(url, json=payload)

if __name__ == "__main__":
    app.run(port=5000)
