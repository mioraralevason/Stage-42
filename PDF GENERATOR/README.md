# Générateur de PDF

## Installation et exécution

1. Donnez les droits d’exécution au script d’installation des dépendances :  
   ```bash
   chmod +x install_dependance.sh
   ```

2. Lancez le script d’installation :  
   ```bash
   ./install_dependance.sh
   ```

3. Activez l’environnement virtuel et exécutez le script Python :  
   ```bash
   source venv/bin/activate && ./pdf.py
   ```
4. Assurez-vous que le script Python pdf.py est exécutable et possède le shebang
   # Ajouter le shebang si nécessaire
   #!/usr/bin/env python3

   # Rendre le script exécutable
   chmod +x pdf.py

## Remarques
- Assurez-vous d’avoir **Python 3** installé sur votre système.  
- Le script `install_dependance.sh` crée un environnement virtuel et installe automatiquement les dépendances nécessaires (`reportlab`, etc.).  
