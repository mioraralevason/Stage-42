# #!/bin/bash

# # Script de déploiement de l'application chaussure-webapp dans Tomcat

# echo "Arrêt de Tomcat..."
# sudo /opt/tomcat10/bin/shutdown.sh

# echo "Attente de 5 secondes pour que Tomcat s'arrête complètement..."
# sleep 5

# echo "Suppression des anciens fichiers de déploiement..."
# sudo rm -rf /opt/tomcat10/webapps/chaussure-webapp* /opt/tomcat10/work/Catalina/localhost/chaussure-webapp*

# echo "Copie du nouveau WAR dans le répertoire webapps de Tomcat..."
# cp /home/tiavina/Documents/GitHub/Stage-42/chaussure/chaussure-project/target/chaussure-webapp.war /opt/tomcat10/webapps/

# echo "Démarrage de Tomcat..."
# sudo /opt/tomcat10/bin/startup.sh

# echo "Attente de 30 secondes pour que Tomcat démarre et déploie l'application..."
# sleep 30

# echo "Vérification de l'accès à l'application..."
# HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/chaussure-webapp/home)

# if [ "$HTTP_CODE" -eq 200 ]; then
#     echo "✅ L'application est accessible à l'adresse : http://localhost:8080/chaussure-webapp/home"
#     echo "✅ Déploiement réussi !"
# elif [ "$HTTP_CODE" -eq 404 ]; then
#     echo "❌ Erreur 404 : L'application n'est pas encore accessible"
#     echo "❌ Vérifiez les logs de Tomcat : sudo tail -f /opt/tomcat10/logs/catalina.out"
# else
#     echo "⚠️  Code de statut HTTP inattendu : $HTTP_CODE"
#     echo "⚠️  Vérifiez les logs de Tomcat : sudo tail -f /opt/tomcat10/logs/catalina.out"
# fi



# Script de déploiement de l'application chaussure-webapp sous Windows (Tomcat)

# ================= CONFIGURATION =================
$TOMCAT_HOME = "C:\xampp\tomcat"
$WAR_SOURCE  = "F:\S5\Mme Baovola\chaussure\chaussure-project\target\chaussure-webapp.war"
$WEBAPPS     = "$TOMCAT_HOME\webapps"
$WORK        = "$TOMCAT_HOME\work\Catalina\localhost"

# ================= STOP TOMCAT =================
Write-Host "Arrêt de Tomcat (XAMPP)..."
& "$TOMCAT_HOME\bin\shutdown.bat"

Write-Host "Attente de 5 secondes..."
Start-Sleep -Seconds 5

# ================= CLEAN DEPLOYMENT =================
Write-Host "Suppression des anciens fichiers de déploiement..."
Remove-Item "$WEBAPPS\chaussure-webapp*" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$WORK\chaussure-webapp*" -Recurse -Force -ErrorAction SilentlyContinue

# ================= COPY WAR =================
Write-Host "Copie du WAR vers Tomcat..."
Copy-Item $WAR_SOURCE $WEBAPPS -Force

# ================= START TOMCAT =================
Write-Host "Démarrage de Tomcat (XAMPP)..."
& "$TOMCAT_HOME\bin\startup.bat"

Write-Host "Attente de 30 secondes pour le déploiement..."
Start-Sleep -Seconds 30

# ================= CHECK APP =================
Write-Host "Vérification de l'accès à l'application..."
try {
    $response = Invoke-WebRequest "http://localhost:8080/chaussure-webapp/home" -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Déploiement réussi"
        Write-Host "➡️ http://localhost:8080/chaussure-webapp/home"
    }
}
catch {
    Write-Host "❌ Application inaccessible"
    Write-Host "➡️ Vérifie les logs : $TOMCAT_HOME\logs\catalina.out"
}
