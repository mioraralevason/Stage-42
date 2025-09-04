


from flask import Flask, request
import requests
import google.generativeai as genai
import os
import urllib.parse

app = Flask(__name__)


genai.configure(api_key="AIzaSyAFDRyfPUkeCvWrUA21Dh1kibkEQQN_r4g")


PAGE_ACCESS_TOKEN = "EAAKm1umLopsBPU76JCQTm6aQ2eCwkiKgH8RcnHgRwBTGl6sz2AZCGxKbM15TmLa6R9loBDEvBIsgj4NCeVwLmZAzZAqsXU3rK9VtcRvIP4AqYnIAXLEKlbuxZB0DzWv9k9RAOz5B7pgqUFLhD5rfURAUamwqWp7sAnZBPQfPtbZBvpc0NmwxnzbFpbIpdkYfVo4tCkewZDZD"
VERIFY_TOKEN = "mon_secret_42"


categories_files = ["inscription.txt","international.txt","admission.txt","formation.txt","piscine.txt","rentree.txt"]
context_text = ""
for file in categories_files:
    if os.path.exists(file):
        with open(file, "r", encoding="utf-8") as f:
            context_text += f"Contenu de {file} :\n" + f.read() + "\n\n"


@app.route("/webhook", methods=["GET"])
def verify():
    token = request.args.get("hub.verify_token")
    challenge = request.args.get("hub.challenge")
    mode = request.args.get("hub.mode")

    if mode == "subscribe" and token == VERIFY_TOKEN:
        print(f"✅ Webhook vérifié avec challenge: {challenge}", flush=True)
        return challenge, 200
    print("❌ Token invalide pour webhook", flush=True)
    return "Token invalide", 403


@app.route("/webhook", methods=["POST"])
def webhook():
    data = request.get_json()
    print("📩 Webhook reçu :", data, flush=True)

    for entry in data.get("entry", []):
        for messaging_event in entry.get("messaging", []):
            sender_id = messaging_event["sender"]["id"]
            user_message = messaging_event.get("message", {}).get("text")
            if user_message:
                print(f"👤 Message de {sender_id} : {user_message}", flush=True)

                
                encoded_question = urllib.parse.quote_plus(user_message)
                google_link = f"https://googlethatforyou.com/?q={encoded_question}"

                
                system_instruction = (
                    "Tu es un assistant spécialisé dans l'école 42 Antananarivo. "
                    "Répond de manière naturelle et claire. "
                    f"Tu dois répondre uniquement avec les informations ci-dessous. "
                    f"Si la question n’est pas liée à ces informations, "
                    f"réponds : 'Je ne peux pas répondre à cette question. Vas sur ce lien : {google_link}'\n\n"
                    "Contexte :\n" + context_text
                )

                try:
                    response = genai.GenerativeModel("gemini-1.5-flash").generate_content(
                        system_instruction + "\n\nQuestion : " + user_message
                    )
                    reply_text = response.text
                except Exception as e:
                    print("⚠️ Erreur Gemini :", e, flush=True)
                    reply_text = f"Je ne peux pas répondre pour le moment. Vas sur ce lien : {google_link}"

                
                send_message(sender_id, reply_text)

    return "ok", 200


def send_message(recipient_id, message_text):
    url = f"https://graph.facebook.com/v17.0/me/messages?access_token={PAGE_ACCESS_TOKEN}"
    payload = {
        "recipient": {"id": recipient_id},
        "message": {"text": message_text}
    }
    try:
        r = requests.post(url, json=payload)
        print("📤 Envoi Messenger :", r.status_code, r.text, flush=True)
    except Exception as e:
        print("❌ Erreur envoi Messenger :", e, flush=True)


if __name__ == "__main__":
    
    app.run(host="0.0.0.0", port=5000)
