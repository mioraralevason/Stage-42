#!/usr/bin/env python3
import tkinter as tk
from tkinter import messagebox
import re
from pdf import generate_certificate, CertificateConfig

class CertificateApp:
    """GUI application for generating school certificates."""
    
    def __init__(self, root):
        self.root = root
        self.root.title("Générateur de PDF")
        self.root.geometry("400x300")
        self.root.resizable(False, False)
        self.root.configure(bg="#f0f0f0")
        self.center_window()
        self.setup_ui()

    def center_window(self):
        """Center the window on the screen."""
        window_width = 400
        window_height = 300
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

        # Liste déroulante pour choisir la signature
        label_signature = tk.Label(main_frame, text="Choisir la signature:", font=font_label, bg="#f0f0f0")
        label_signature.pack(pady=(10, 5))

        self.signature_var = tk.StringVar(value="Directeur")  # Valeur par défaut
        signature_options = ["Directeur", "Assistant"]
        signature_menu = tk.OptionMenu(main_frame, self.signature_var, *signature_options)
        signature_menu.config(font=font_entry, width=15, bg="white")
        signature_menu.pack(pady=5)

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
        """Fetch user data and generate certificate."""
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
            # Generate certificate using login and selected signature
            config = CertificateConfig()
            generate_certificate(login=login, signer_par=self.signature_var.get())

            success_msg = f"PDF généré pour l'utilisateur {login}: {config.output_file}"
            print(success_msg)
            messagebox.showinfo("Succès", success_msg)

        except ValueError as e:
            error_msg = f"Login invalide: {str(e)}"
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