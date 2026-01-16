<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ajouter une Chaussure - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Ajoute ton CSS si besoin -->
</head>
<body>
    <h1>Ajouter une nouvelle chaussure</h1>
    
    <form method="post" action="${pageContext.request.contextPath}/admin">
        <input type="hidden" name="action" value="add">
        
        <label>Nom: <input type="text" name="nom" required></label><br>
        <label>Prix: <input type="number" name="prix" step="0.01" required></label><br>
        <label>Description: <textarea name="description"></textarea></label><br>
        <label>Stock: <input type="number" name="stock" required></label><br>
        <label>Photo (nom fichier): <input type="text" name="photo"></label><br>
        
        <label>Marque:
            <select name="marqueId" required>
                <option value="">Choisir</option>
                <% for(String[] marque : (java.util.List<String[]>) request.getAttribute("marques")) { %>
                    <option value="<%= marque[0] %>"><%= marque[1] %></option>
                <% } %>
            </select>
        </label><br>
        
        <label>Catégorie:
            <select name="categorieId" required>
                <option value="">Choisir</option>
                <% for(String[] cat : (java.util.List<String[]>) request.getAttribute("categories")) { %>
                    <option value="<%= cat[0] %>"><%= cat[1] %></option>
                <% } %>
            </select>
        </label><br>
        
        <label>Type:
            <select name="typeId" required>
                <option value="">Choisir</option>
                <% for(String[] type : (java.util.List<String[]>) request.getAttribute("types")) { %>
                    <option value="<%= type[0] %>"><%= type[1] %></option>
                <% } %>
            </select>
        </label><br>
        
        <label>Taille:
            <select name="tailleId" required>
                <option value="">Choisir</option>
                <% for(String[] taille : (java.util.List<String[]>) request.getAttribute("tailles")) { %>
                    <option value="<%= taille[0] %>"><%= taille[1] %></option>
                <% } %>
            </select>
        </label><br>
        
        <label>Couleur:
            <select name="couleurId" required>
                <option value="">Choisir</option>
                <% for(String[] couleur : (java.util.List<String[]>) request.getAttribute("couleurs")) { %>
                    <option value="<%= couleur[0] %>"><%= couleur[1] %></option>
                <% } %>
            </select>
        </label><br>
        
        <button type="submit">Ajouter</button>
    </form>
    
    <a href="${pageContext.request.contextPath}/admin">Retour à la liste</a>
</body>
</html>