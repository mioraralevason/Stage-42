# Installation
# pip install google-generativeai

import google.generativeai as genai
import os
import urllib.parse  # pour encoder correctement l'URL

# Configurer la clé API
genai.configure(api_key="AIzaSyAFDRyfPUkeCvWrUA21Dh1kibkEQQN_r4g")

# Liste des fichiers correspondant aux catégories
categories_files = [
    "inscription.txt",
    "international.txt",
    "admission.txt",
    "formation.txt",
    "piscine.txt",
    "rentree.txt"
]

# Lire et concaténer le contenu des fichiers
context_text = ""
for file in categories_files:
    if os.path.exists(file):
        with open(file, "r", encoding="utf-8") as f:
            context_text += f"Contenu de {file} :\n" + f.read() + "\n\n"

# Question de l'utilisateur
question = "Qui est Andry Rajoelina"

# Encoder la question pour l'URL
encoded_question = urllib.parse.quote_plus(question)
google_link = f"https://googlethatforyou.com/?q={encoded_question}"

# Prompt avec règles strictes
system_instruction = (
    "Tu es un assistant spécialisé dans l'école 42 Antananarivo. "
    "Répond de manière naturelle et bien claire. "
    "Tu dois répondre uniquement en utilisant les informations fournies ci-dessous. "
    f"Si la question n’est pas liée à ces informations ou si tu n’y trouves pas la réponse, "
    f"réponds uniquement : 'Je ne peux pas répondre à cette question. Vas sur ce lien : {google_link}'\n\n"
    "Contexte :\n" + context_text
)

# Envoyer le prompt
response = genai.GenerativeModel("gemini-1.5-flash").generate_content(
    system_instruction + "\n\nQuestion : " + question
)

print(response.text)
