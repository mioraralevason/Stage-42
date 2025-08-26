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

class CertificateConfig:
    """Configuration class for certificate settings."""
    
    def __init__(self):
        load_dotenv()
        self.logo = os.getenv("LOGO", "Inconnu.png")
        self.tampon = os.getenv("TAMPON", "Inconnu.png")
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

    def generate(self, nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame="Monsieur"):
        """Generates the certificate PDF."""
        elements = self.create_content(nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame)
        self.doc.build(elements, onFirstPage=self.draw_page, onLaterPages=self.draw_page)
        print(f"PDF generated: {self.config.output_file}")

def generate_certificate(nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame="Monsieur"):
    """Main function to generate a school certificate."""
    config = CertificateConfig()
    generator = CertificateGenerator(config)
    generator.generate(nom, prenom, date_naissance, lieu_naissance, adresse, nationalite, zip_code, ville, monsieur_madame)

if __name__ == "__main__":
    # Example usage
    generate_certificate(
        nom="Doe",
        prenom="John",
        date_naissance="15-05-2000",
        lieu_naissance="Paris",
        adresse="123 Rue Exemple",
        nationalite="Française",
        zip_code="75001",
        ville="Paris"
    )