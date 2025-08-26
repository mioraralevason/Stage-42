#!/usr/bin/env python3
import os
import re
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pdf import generate_certificate
from api import get_id_users, get_user, get_user_candidature

# Initialize FastAPI app
app = FastAPI(title="42 Certificate API", description="API for generating school certificates using 42 API data")

@app.get("/get_id_users/{login}")
async def api_get_id_users(login: str):
    """Endpoint to get user ID by login."""
    try:
        user_id = get_id_users(login)
        return {"login": login, "user_id": user_id}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur: {str(e)}")

@app.get("/get_user/{user_id}")
async def api_get_user(user_id: str):
    """Endpoint to get user data by user ID."""
    try:
        user_data = get_user(user_id)
        return user_data
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur: {str(e)}")

@app.get("/get_user_candidature/{user_id}")
async def api_get_user_candidature(user_id: str):
    """Endpoint to get candidature data by user ID."""
    try:
        candidature_data = get_user_candidature(user_id)
        return candidature_data
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur: {str(e)}")

@app.get("/generate_certificate/{login}", response_class=FileResponse)
async def api_generate_certificate(login: str, signer_par: str = "Aucune"):
    """Endpoint to generate and return a PDF certificate for a given login."""
    # Basic login validation (alphanumeric and underscores only)
    if not login:
        raise HTTPException(status_code=400, detail="Le login ne peut pas être vide")
    if not re.match(r'^[a-zA-Z0-9_]+$', login):
        raise HTTPException(status_code=400, detail="Le login ne doit contenir que des lettres, chiffres ou underscores")

    try:
        # Validate signer_par
        if signer_par not in ["Directeur", "Assistant", "Aucune"]:
            raise HTTPException(status_code=400, detail="La signature doit être 'Directeur', 'Assistant' ou 'Aucune'")

        # Generate certificate and get the output filename
        output_file = generate_certificate(login=login, signer_par=signer_par)

        # Return the generated PDF as a file response
        if not os.path.exists(output_file):
            raise HTTPException(status_code=500, detail=f"Erreur lors de la génération du PDF: fichier non trouvé à {output_file}")
        
        print(f"PDF generated for user {login}: {output_file}")
        return FileResponse(
            output_file,
            media_type="application/pdf",
            filename=f"certificat_scolarite_{login}.pdf"
        )

    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur inattendue: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", 8000))
    uvicorn.run(app, host="0.0.0.0", port=port)