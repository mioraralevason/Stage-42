#!/usr/bin/env python3

from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from datetime import datetime
from dotenv import load_dotenv
import os
import uuid
from api import get_access_token, get_id_users, get_user, get_user_candidature
from nationalities import country_to_nationality

class CertificateConfig:
    """Configuration class for certificate settings."""
    
    def __init__(self):
        load_dotenv()
        self.logo = os.getenv("LOGO", "Inconnu.png")
        self.tampon = os.getenv("TAMPON", "Inconnu.png")
        self.signature_directeur = os.getenv("SIGNATURE_DIRECTEUR", "Inconnu.png")
        self.signature_assistant = os.getenv("SIGNATURE_ASSISTANT", "Inconnu.png")
        self.responsable = os.getenv("RESPONSABLE", "Inconnu")
        self.poste = os.getenv("POSTE", "Inconnu")
        self.etablissement = os.getenv("ETABLISSEMENT", "42")
        self.etablissement_adresse = os.getenv("ETABLISSEMENT_ADRESSE", "Inconnu")
        self.output_dir = "pdf"
        self.output_file = os.path.join(self.output_dir, "certificat_scolarite.pdf")

class CertificateStyles:
    """Styles for the certificate document."""
    
    @staticmethod
    def get_styles():
        styles = getSampleStyleSheet()
        return {
            'title': ParagraphStyle(
                name='TitleCustom',
                fontSize=16,
                leading=20,
                alignment=TA_CENTER,
                spaceAfter=20
            ),
            'body': ParagraphStyle(
                name='BodyCustom',
                fontSize=12,
                leading=14,
                alignment=TA_LEFT,
                spaceAfter=12
            ),
            'footer': ParagraphStyle(
                name='FooterCustom',
                fontSize=10,
                leading=12,
                alignment=TA_CENTER,
                spaceBefore=20
            )
        }

class CertificateGenerator:
    """Generates a school certificate PDF."""
    
    def __init__(self, config):
        self.config = config
        os.makedirs(self.config.output_dir, exist_ok=True)
        self.doc = SimpleDocTemplate(
            self.config.output_file,
            pagesize=A4,
            leftMargin=2*cm,
            rightMargin=2*cm,
            topMargin=4*cm,
            bottomMargin=4*cm
        )
        self.styles = CertificateStyles.get_styles()

    def draw_page(self, canvas, doc):
        """Draws images on each page."""
        canvas.drawImage(
            self.config.logo,
            2*cm,
            A4[1] - 4*cm,
            width=A4[0] - 4*cm,
            height=4*cm,
            preserveAspectRatio=True
        )
        canvas.drawImage(
            self.config.tampon,
            8*cm,
            6*cm,
            width=A4[0] - 6.01*cm,
            height=3.73*cm,
            preserveAspectRatio=True
        )
        if self.signature == "Directeur":
            canvas.drawImage(
                self.config.signature_directeur,
                2*cm,
                6*cm,
                width=6*cm,
                height=3*cm,
                preserveAspectRatio=True
            )
        else:
            canvas.drawImage(
                self.config.signature_assistant,
                2*cm,
                6*cm,
                width=6*cm,
                height=3*cm,
                preserveAspectRatio=True
            )

    def create_content(self, nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame):
        """Creates the content for the certificate."""
        elements = []
        current_date = datetime.now().strftime("%d/%m/%Y")
        current_year = current_date.split("/")[-1]
        date_obj = datetime.strptime(date_naissance, "%d-%m-%Y")
        date_naissance = date_obj.strftime("%d/%m/%Y")
        interesse = "l’intéressé" if monsieur_madame == "Monsieur" else "l’intéressée"
        inscrit = "inscrit" if monsieur_madame == "Monsieur" else "inscrite"

        # Title
        elements.append(Paragraph("Certificat de scolarité", self.styles['title']))
        elements.append(Spacer(1, 0.5*cm))

        # Body
        content_text = (
            f"Je soussigné, Monsieur <b>{self.config.responsable}, {self.config.poste}</b> "
            f"de l’établissement {self.config.etablissement}, domicilié au {self.config.etablissement_adresse}, "
            f"atteste que l’élève :<br/><br/>"
            f"<b>{monsieur_madame}</b><br/>"
            f"<b>Nom</b> : {nom}<br/>"
            f"<b>Prénom</b> : {prenom}<br/>"
            f"<b>Date de naissance</b> : {date_naissance}<br/>"
            f"<b>Lieu de naissance</b> : {lieu_naissance}<br/>"
            f"<b>Nationalité</b> : {nationalite}<br/>"
            f"<b>Adresse</b> : {adresse}, {zip_code} {ville}<br/><br/>"
            f"Est régulièrement {inscrit} pour l’année {current_year} à l’école {self.config.etablissement}, "
            f"école gratuite sans frais d’écolage, et n’y a pas encore fini ses études.<br/><br/>"
            f"Cette attestation est délivrée le {current_date} à la demande de {interesse} "
            f"pour servir et faire valoir ce que de droit."
        )
        elements.append(Paragraph(content_text, self.styles['body']))

        # Footer
        footer_text = (
            f"BY <font color='#1DD7F2'><u>{self.config.etablissement}</u></font><br/>"
            f"{self.config.etablissement_adresse}"
        )
        elements.append(Spacer(1, 2*cm))
        elements.append(Paragraph(footer_text, self.styles['footer']))

        return elements

    def generate(self, nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame="Monsieur", signer_par="Directeur"):
        """Generates the certificate PDF."""
        elements = self.create_content(nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame)
        self.signature = signer_par
        self.doc.build(elements, onFirstPage=self.draw_page, onLaterPages=self.draw_page)
        print(f"PDF generated: {self.config.output_file}")

def generate_certificate(login, signer_par="Directeur"):
    """Main function to generate a school certificate based on user login."""
    config = CertificateConfig()
    # Update output filename to be unique
    config.output_file = os.path.join(config.output_dir, f"certificat_scolarite_{login}_{uuid.uuid4().hex}.pdf")
    
    try:
        # Get user ID from login
        user_id = get_id_users(login)
        print(f"User ID for login {login}: {user_id}")

        # Fetch user and candidature data
        token = get_access_token()
        info_data = get_user(user_id, token)
        user_data = get_user_candidature(user_id, token)
        print(f"User data: {info_data}")
        print(f"Candidature data: {user_data}")

        # Convert birth_date from YYYY-MM-DD to DD-MM-YYYY
        birth_date = user_data.get("birth_date", None)
        if not birth_date:
            warning_msg = f"Aucune date de naissance trouvée pour {login}, utilisation de la date par défaut '2000-01-01'"
            print(warning_msg)
            birth_date = "2000-01-01"

        try:
            date_obj = datetime.strptime(birth_date, "%Y-%m-%d")
            formatted_birth_date = date_obj.strftime("%d-%m-%Y")
        except ValueError as e:
            raise ValueError(f"Format de date de naissance invalide: {str(e)}")

        # Handle None values for other fields
        lieu_naissance = user_data.get("birth_city", "Inconnu") or "Inconnu"
        adresse = user_data.get("postal_street", "Inconnu") or "Inconnu"
        zip_code = user_data.get("postal_zip_code", "00000") or "00000"
        ville = user_data.get("postal_city", "Inconnu") or "Inconnu"
        nationalite = country_to_nationality.get(user_data.get("country", "Inconnu"), "Inconnue")

        # Generate certificate
        generator = CertificateGenerator(config)
        generator.generate(
            nom=info_data.get("last_name", "Inconnu"),
            prenom=info_data.get("first_name", "Inconnu"),
            date_naissance=formatted_birth_date,
            lieu_naissance=lieu_naissance,
            adresse=adresse,
            nationalite=nationalite,
            zip_code=zip_code,
            ville=ville,
            monsieur_madame="Monsieur" if user_data.get("gender") == "male" else "Madame",
            signer_par=signer_par
        )

    except Exception as e:
        print(f"Erreur lors de la génération du certificat pour {login}: {str(e)}")
        raise

if __name__ == "__main__":
    # Example usage
    generate_certificate(login="mralevas", signer_par="Directeur")