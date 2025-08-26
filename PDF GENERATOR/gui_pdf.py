#!/usr/bin/env python3
import tkinter as tk
from tkinter import messagebox
from datetime import datetime
import os
import uuid
import requests
import re
from pdf import generate_certificate, CertificateConfig
from api import get_access_token, get_user, get_user_candidature, get_id_users
from nationalities import country_to_nationality

class CertificateApp:
    """GUI application for generating school certificates."""
    
    def __init__(self, root):
        self.root = root
        self.root.title("Générateur de PDF")
        self.root.geometry("400x250")
        self.root.resizable(False, False)
        self.root.configure(bg="#f0f0f0")
        self.center_window()
        self.setup_ui()

    def center_window(self):
        """Center the window on the screen."""
        window_width = 400
        window_height = 250
        screen_width = self.root.winfo_screenwidth()
        screen_height = self.root.winfo_screenheight()
        center_x = int(screen_width / 2 - window_width / 2)
        center_y = int(screen_height / 2 - window_height / 2)
        self.root.geometry(f'{window_width}x{window_height}+{center_x}+{center_y}')

    def setup_ui(self):
        """Set up the GUI components."""
        main_frame = tk.Frame(self.root, bg="#f0f0f0")
        main_frame.pack(expand=True)

        # Styles
        font_label = ("Helvetica", 12)
        font_entry = ("Helvetica", 12)
        font_button = ("Helvetica", 12, "bold")
        font_title = ("Helvetica", 16, "bold")

        # Widgets
        title_label = tk.Label(main_frame, text="Générateur de PDF", font=font_title, bg="#f0f0f0")
        title_label.pack(pady=(20, 10))

        label = tk.Label(main_frame, text="Entrez le login de l'utilisateur:", font=font_label, bg="#f0f0f0")
        label.pack(pady=(10, 5))

        self.entry_login = tk.Entry(main_frame, font=font_entry, width=20, justify='center')
        self.entry_login.pack(pady=5, ipady=4)

        button = tk.Button(
            main_frame,
            text="Générer PDF",
            command=self.fetch_and_generate,
            font=font_button,
            bg="#007BFF",
            fg="white",
            activebackground="#0056b3",
            activeforeground="white",
            relief="flat",
            padx=10,
            pady=5,
            cursor="hand2"
        )
        button.pack(pady=20)

        # Hover effects
        button.bind("<Enter>", lambda e: button.configure(background='#0056b3'))
        button.bind("<Leave>", lambda e: button.configure(background='#007BFF'))

    def fetch_and_generate(self):
        """Fetch user data from API and generate certificate."""
        login = self.entry_login.get().strip()
        if not login:
            error_msg = "Le login ne peut pas être vide"
            print(f"Erreur: {error_msg}")
            messagebox.showerror("Erreur", error_msg)
            return

        # Basic login validation (alphanumeric and underscores only)
        if not re.match(r'^[a-zA-Z0-9_]+$', login):
            error_msg = "Le login ne doit contenir que des lettres, chiffres ou underscores"
            print(f"Erreur: {error_msg}")
            messagebox.showerror("Erreur", error_msg)
            return

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
                error_msg = f"Format de date de naissance invalide: {str(e)}"
                print(f"Erreur: {error_msg}")
                messagebox.showerror("Erreur", error_msg)
                return

            # Handle None values for other fields
            lieu_naissance = user_data.get("birth_city", "Inconnu") or "Inconnu"
            adresse = user_data.get("postal_street", "Inconnu") or "Inconnu"
            zip_code = user_data.get("postal_zip_code", "00000") or "00000"
            ville = user_data.get("postal_city", "Inconnu") or "Inconnu"
            nationalite = country_to_nationality.get(user_data.get("country", "Inconnu"), "Inconnue")

            # Update output filename to be unique
            config = CertificateConfig()
            config.output_file = os.path.join(config.output_dir, f"certificat_scolarite_{user_id}_{uuid.uuid4().hex}.pdf")

            # Generate certificate
            generate_certificate(
                nom=info_data.get("last_name", "Inconnu"),
                prenom=info_data.get("first_name", "Inconnu"),
                date_naissance=formatted_birth_date,
                lieu_naissance=lieu_naissance,
                adresse=adresse,
                nationalite=nationalite,
                zip_code=zip_code,
                ville=ville,
                monsieur_madame="Monsieur" if info_data.get("gender") == "male" else "Madame"
            )

            success_msg = f"PDF généré pour l'utilisateur {login}: {config.output_file}"
            print(success_msg)
            messagebox.showinfo("Succès", success_msg)

        except ValueError as e:
            error_msg = f"Login invalide: {str(e)}"
            print(f"Erreur: {error_msg}")
            messagebox.showerror("Erreur", error_msg)
        except requests.RequestException as e:
            error_msg = f"Erreur API lors de la récupération des données: {str(e)}"
            print(f"Erreur: {error_msg}")
            messagebox.showerror("Erreur", error_msg)
        except Exception as e:
            error_msg = f"Erreur inattendue: {str(e)}"
            print(f"Erreur: {error_msg}")
            messagebox.showerror("Erreur", error_msg)

if __name__ == "__main__":
    root = tk.Tk()
    app = CertificateApp(root)
    root.mainloop()