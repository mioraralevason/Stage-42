<%-- <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title} - Application de Gestion de Chaussures</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="container">
        <!-- Barre latérale -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <h2><i class="fas fa-store"></i> Chaussure Shop</h2>
            </div>
            <ul class="sidebar-menu">
                <li><a href="${pageContext.request.contextPath}/home"><i class="fas fa-home"></i> <span>Accueil</span></a></li>
                <li><a href="${pageContext.request.contextPath}/chaussures"><i class="fas fa-shoe-prints"></i> <span>Vente Chaussures</span></a></li>
            </ul>
        </nav>

        <!-- Contenu principal -->
        <div class="main-content">
            <header class="header">
                <h1>${title}</h1>
                <div class="user-info">
                    <img src="${pageContext.request.contextPath}/images/user-placeholder.png" alt="Utilisateur">
                    <span>Administrateur</span>
                </div>
            </header>

            <main class="content-area">
                <h2>${subtitle}</h2>
                <p>Gestion des produits de la boutique de chaussures.</p>
                
                <div style="margin-top: 20px;">
                    <table style="width:100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background-color: #f2f2f2;">
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">ID</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Nom</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Prix</th>
                                <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style="padding: 10px; border: 1px solid #ddd;">1</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">Baskets Mode</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">89,99 €</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">50</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 10px; border: 1px solid #ddd;">2</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">Chaussures de Sport</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">129,99 €</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">30</td>
                            </tr>
                            <tr>
                                <td style="padding: 10px; border: 1px solid #ddd;">3</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">Derby Classique</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">159,99 €</td>
                                <td style="padding: 10px; border: 1px solid #ddd;">20</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    </div>
</body>
</html> --%>




<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion Produits - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .container { display: flex; min-height: 100vh; }
        .sidebar { width: 250px; background: #2c3e50; color: white; padding: 20px; }
        .main-content { flex: 1; padding: 30px; background: #f8f9fa; }
        .form-section, .list-section { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        label { display: block; margin: 12px 0 6px; font-weight: bold; }
        input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }
        button { background: #27ae60; color: white; border: none; padding: 12px 24px; border-radius: 5px; cursor: pointer; margin-top: 15px; }
        button:hover { background: #219653; }
        .message { padding: 12px; border-radius: 5px; margin-bottom: 20px; }
        .success { background: #d4edda; color: #155724; }
        .error   { background: #f8d7da; color: #721c24; }
        .product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 25px; }
        .product-card { background: white; border: 1px solid #ddd; border-radius: 8px; padding: 15px; text-align: center; }
        .product-card img { width: 100%; height: 160px; object-fit: cover; border-radius: 5px; }
    </style>
</head>
<body>

<div class="container">
    <nav class="sidebar">
        <h2><i class="fas fa-user-shield"></i> Admin</h2>
        <ul style="list-style:none; padding:0;">
            <li><a href="${pageContext.request.contextPath}/admin" class="active">Produits</a></li>
            <li><a href="#">Commandes</a></li>
            <li><a href="#">Utilisateurs</a></li>
            <li><a href="${pageContext.request.contextPath}/logout">Déconnexion</a></li>
        </ul>
    </nav>

    <div class="main-content">
        <h1>Gestion des Chaussures</h1>

        <!-- Messages -->
        <c:if test="${not empty message}">
            <div class="message ${messageType}">
                ${message}
            </div>
        </c:if>

        <!-- Formulaire d'ajout (toujours visible) -->
        <div class="form-section">
            <h2>Ajouter une nouvelle chaussure</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin">
                <input type="hidden" name="action" value="add">

                <label>Nom *</label>
                <input type="text" name="nom" required>

                <label>Prix (€) *</label>
                <input type="number" name="prix" step="0.01" min="0" required>

                <label>Description</label>
                <textarea name="description" rows="3"></textarea>

                <label>Stock initial *</label>
                <input type="number" name="stock" min="0" required>

                <label>Photo (nom fichier)</label>
                <input type="text" name="photo" placeholder="ex: nike-air-max.jpg">

                <label>Marque *</label>
                <select name="marqueId" required>
                    <option value="">-- Choisir --</option>
                    <c:forEach var="m" items="${marques}">
                        <option value="${m[0]}">${m[1]}</option>
                    </c:forEach>
                </select>

                <label>Catégorie *</label>
                <select name="categorieId" required>
                    <option value="">-- Choisir --</option>
                    <c:forEach var="c" items="${categories}">
                        <option value="${c[0]}">${c[1]}</option>
                    </c:forEach>
                </select>

                <label>Type *</label>
                <select name="typeId" required>
                    <option value="">-- Choisir --</option>
                    <c:forEach var="t" items="${types}">
                        <option value="${t[0]}">${t[1]}</option>
                    </c:forEach>
                </select>

                <label>Taille *</label>
                <select name="tailleId" required>
                    <option value="">-- Choisir --</option>
                    <c:forEach var="t" items="${tailles}">
                        <option value="${t[0]}">${t[1]}</option>
                    </c:forEach>
                </select>

                <label>Couleur *</label>
                <select name="couleurId" required>
                    <option value="">-- Choisir --</option>
                    <c:forEach var="co" items="${couleurs}">
                        <option value="${co[0]}">${co[1]}</option>
                    </c:forEach>
                </select>

                <button type="submit"><i class="fas fa-save"></i> Ajouter la chaussure</button>
            </form>
        </div>

        <!-- Liste des chaussures -->
        <div class="list-section">
            <h2>Liste des chaussures (${chaussures.size()})</h2>

            <div class="product-grid">
                <c:forEach var="c" items="${chaussures}">
                    <div class="product-card">
                        <img src="${pageContext.request.contextPath}/images/${empty c.photo ? 'placeholder_shoe.jpg' : c.photo}" 
                             alt="${c.nom}">
                        <h4>${c.nom}</h4>
                        <p><strong>${c.prix} €</strong></p>
                        <p>Stock : ${c.stock}</p>
                        <small>${c.marque} • ${c.categorie} • ${c.type}</small>
                    </div>
                </c:forEach>

                <c:if test="${empty chaussures}">
                    <p style="text-align:center; color:#777; grid-column:1/-1;">
                        Aucune chaussure pour le moment.
                    </p>
                </c:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>