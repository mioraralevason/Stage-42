# Application de Gestion de Chaussures

Une application web professionnelle de gestion de chaussures développée avec Java Servlet/JSP et Jakarta EE.

## Technologies utilisées

- Java 11+
- Jakarta EE 10
- Servlet API 6.0
- JSP (JavaServer Pages)
- JSTL (Jakarta Standard Tag Library)
- Maven 3+
- Apache Tomcat 10

## Structure du projet

- `src/main/java/com/example/servlet/` - Classes servlet
- `src/main/webapp/` - Ressources web (JSP, CSS, JS)
- `src/main/webapp/jsp/` - Pages JSP
- `src/main/webapp/css/` - Feuilles de style
- `src/main/webapp/js/` - Scripts JavaScript
- `src/main/webapp/images/` - Images

## Prérequis

- Java 11 ou supérieur
- Apache Tomcat 10
- Maven 3.6 ou supérieur

## Installation et exécution

### 1. Compiler le projet

```bash
cd /chemin/vers/chaussure-project
mvn clean package
```

### 2. Déployer dans Tomcat

1. Arrêter Tomcat s'il est en cours d'exécution :
```bash
sudo /opt/tomcat10/bin/shutdown.sh
```

2. Supprimer les anciens fichiers de déploiement :
```bash
sudo rm -rf /opt/tomcat10/webapps/chaussure-webapp* /opt/tomcat10/work/Catalina/localhost/chaussure-webapp*
```

3. Copier le nouveau fichier WAR dans le répertoire webapps de Tomcat :
```bash
cp target/chaussure-webapp.war /opt/tomcat10/webapps/
```

4. Démarrer Tomcat :
```bash
sudo /opt/tomcat10/bin/startup.sh
```

### 3. Accéder à l'application

Attendre quelques secondes que Tomcat déploie l'application, puis accéder à :

- Page d'accueil : [http://localhost:8080/chaussure-webapp/home](http://localhost:8080/chaussure-webapp/home)
- Produits : [http://localhost:8080/chaussure-webapp/products](http://localhost:8080/chaussure-webapp/products)

## Fonctionnalités

- Interface professionnelle avec barre latérale (sidebar)
- Système de navigation
- Pages d'exemples pour la gestion des produits
- Design responsive

## Configuration

Le fichier de configuration principal est `src/main/webapp/WEB-INF/web.xml` qui définit :
- Les servlets et leurs mappings URL
- Le fichier d'accueil
- Les paramètres généraux de l'application

## Dépannage

### Codes d'erreurs fréquents

- **404 Not Found** : Vérifiez que Tomcat est démarré et que le fichier WAR a été correctement déployé
- **500 Internal Server Error** : Consultez les logs dans `/opt/tomcat10/logs/catalina.out`
- **Port 8080 occupé** : Vérifiez qu'aucune autre instance de Tomcat ne tourne pas

### Consulter les logs

```bash
sudo tail -f /opt/tomcat10/logs/catalina.out
```

### Redémarrer Tomcat

```bash
sudo /opt/tomcat10/bin/shutdown.sh
sudo /opt/tomcat10/bin/startup.sh
```

## Notes

- L'application est configurée pour fonctionner avec Jakarta EE 10 (Tomcat 10)
- Les servlets sont déclarées dans le fichier web.xml pour éviter les conflits
- Le template inclut une sidebar professionnelle et des CSS modernes