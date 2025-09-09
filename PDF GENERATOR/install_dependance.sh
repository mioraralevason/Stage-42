#!/bin/bash

# Exit on any error
set -e

# Update package list and install system dependencies
echo "Updating package list and installing system dependencies..."
sudo apt update
sudo apt install -y python3.12 python3.12-venv python3-pip

# Create and activate a virtual environment
echo "Creating and activating virtual environment..."
python3 -m venv venv
source venv/bin/activate

# Upgrade pip in the virtual environment
echo "Upgrading pip..."
pip install --upgrade pip

# Install Python dependencies
echo "Installing Python dependencies (reportlab, Pillow)..."
pip install reportlab pillow
pip install python-dotenv
sudo apt install python3-tk
pip install requests
pip install fastapi uvicorn
pip install requests requests_oauthlib
pip install google-auth-oauthlib google-auth-httplib2
pip install aiohttp


# Verify installations
echo "Verifying installed packages..."
pip show reportlab
pip show pillow

# Deactivate virtual environment
echo "Deactivating virtual environment..."
deactivate

# Check for image files
echo "Checking for required image files..."
for img in "./images/logo.png" "./images/tampon.png" "./images/signature_directeur.png" "./images/signature_assistant.png"; do
    if [ -f "$img" ]; then
        echo "Found: $img"
    else
        echo "ERROR: Image file not found: $img"
        echo "Please ensure the images exist at the specified paths."
    fi
done

echo "Dependencies installed successfully. Run the script with:"
echo "source venv/bin/activate && ./pdf.py"