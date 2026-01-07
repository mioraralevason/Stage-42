#!/bin/bash

# Script de déploiement de l'application chaussure-webapp dans Tomcat

echo "Arrêt de Tomcat..."
sudo /opt/tomcat10/bin/shutdown.sh

echo "Attente de 5 secondes pour que Tomcat s'arrête complètement..."
sleep 5

echo "Suppression des anciens fichiers de déploiement..."
sudo rm -rf /opt/tomcat10/webapps/chaussure-webapp* /opt/tomcat10/work/Catalina/localhost/chaussure-webapp*

echo "Copie du nouveau WAR dans le répertoire webapps de Tomcat..."
cp /home/tiavina/Documents/GitHub/Stage-42/chaussure/chaussure-project/target/chaussure-webapp.war /opt/tomcat10/webapps/

echo "Démarrage de Tomcat..."
sudo /opt/tomcat10/bin/startup.sh

echo "Attente de 30 secondes pour que Tomcat démarre et déploie l'application..."
sleep 30

echo "Vérification de l'accès à l'application..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/chaussure-webapp/home)

if [ "$HTTP_CODE" -eq 200 ]; then
    echo "✅ L'application est accessible à l'adresse : http://localhost:8080/chaussure-webapp/home"
    echo "✅ Déploiement réussi !"
elif [ "$HTTP_CODE" -eq 404 ]; then
    echo "❌ Erreur 404 : L'application n'est pas encore accessible"
    echo "❌ Vérifiez les logs de Tomcat : sudo tail -f /opt/tomcat10/logs/catalina.out"
else
    echo "⚠️  Code de statut HTTP inattendu : $HTTP_CODE"
    echo "⚠️  Vérifiez les logs de Tomcat : sudo tail -f /opt/tomcat10/logs/catalina.out"
fi