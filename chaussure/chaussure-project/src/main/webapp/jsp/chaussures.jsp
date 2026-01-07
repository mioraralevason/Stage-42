<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title} - Application de Gestion de Chaussures</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        .product-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            background: white;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .product-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 5px;
            margin-bottom: 10px;
        }

        .product-price {
            font-weight: bold;
            color: #e74c3c;
            font-size: 1.2em;
            margin: 10px 0;
        }

        .product-stock {
            color: #27ae60;
            font-size: 0.9em;
        }

        .btn-add-to-cart {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-add-to-cart:hover {
            background-color: #2980b9;
        }

        .cart-summary {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 15px;
            margin-top: 20px;
        }

        .filter-section {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .filter-row {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 10px;
        }

        .filter-item {
            flex: 1;
            min-width: 150px;
        }

        .filter-item label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        .filter-item input, .filter-item select {
            width: 100%;
            padding: 5px;
            border: 1px solid #ccc;
            border-radius: 3px;
        }

        .search-bar {
            margin-bottom: 15px;
        }

        .search-bar input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .cart-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            border-bottom: 1px solid #eee;
        }

        .cart-actions {
            display: flex;
            gap: 5px;
        }

        .btn-cart-action {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 3px;
            cursor: pointer;
        }

        .btn-cart-action.update {
            background-color: #3498db;
        }

        .btn-cart-action.clear {
            background-color: #f39c12;
        }
    </style>
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
                <li><a href="${pageContext.request.contextPath}/chaussures" class="active"><i class="fas fa-shoe-prints"></i> <span>Vente Chaussures</span></a></li>
            </ul>
        </nav>

        <!-- Contenu principal -->
        <div class="main-content">
            <header class="header">
                <h1>${title}</h1>
                <div class="user-info">
                    <img src="${pageContext.request.contextPath}/images/user-placeholder.png" alt="Utilisateur">
                    <span>Client</span>
                </div>
            </header>

            <main class="content-area">
                <h2>${subtitle}</h2>

                <!-- Section des filtres -->
                <div class="filter-section">
                    <h3>Filtrer les chaussures</h3>

                    <%
                        // Récupérer les paramètres pour les comparer avec les options
                        String marqueParam = request.getParameter("marque");
                        String categorieParam = request.getParameter("categorie");
                        String typeParam = request.getParameter("type");
                        String tailleParam = request.getParameter("taille");
                        String couleurParam = request.getParameter("couleur");
                        String keywordParam = request.getParameter("keyword");

                        // Initialiser les valeurs par défaut
                        if(keywordParam == null) keywordParam = "";
                        if(marqueParam == null) marqueParam = "";
                        if(categorieParam == null) categorieParam = "";
                        if(typeParam == null) typeParam = "";
                        if(tailleParam == null) tailleParam = "";
                        if(couleurParam == null) couleurParam = "";
                    %>

                    <form method="get" action="${pageContext.request.contextPath}/chaussures">
                        <input type="hidden" name="action" value="view">

                        <div class="search-bar">
                            <input type="text" name="keyword" placeholder="Rechercher une chaussure..." value="<%= keywordParam %>">
                        </div>

                        <div class="filter-row">
                            <div class="filter-item">
                                <label for="marque">Marque:</label>
                                <select id="marque" name="marque">
                                    <option value="">Toutes les marques</option>
                                    <%
                                        java.util.List<String[]> marques = (java.util.List<String[]>) request.getAttribute("marques");
                                        if(marques != null) {
                                            for(String[] marque : marques) {
                                                String selected = marqueParam.equals(marque[0]) ? "selected" : "";
                                    %>
                                                <option value="<%= marque[0] %>" <%= selected %>><%= marque[1] %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="filter-item">
                                <label for="categorie">Catégorie:</label>
                                <select id="categorie" name="categorie">
                                    <option value="">Toutes les catégories</option>
                                    <%
                                        java.util.List<String[]> categories = (java.util.List<String[]>) request.getAttribute("categories");
                                        if(categories != null) {
                                            for(String[] categorie : categories) {
                                                String selected = categorieParam.equals(categorie[0]) ? "selected" : "";
                                    %>
                                                <option value="<%= categorie[0] %>" <%= selected %>><%= categorie[1] %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="filter-item">
                                <label for="type">Type:</label>
                                <select id="type" name="type">
                                    <option value="">Tous les types</option>
                                    <%
                                        java.util.List<String[]> types = (java.util.List<String[]>) request.getAttribute("types");
                                        if(types != null) {
                                            for(String[] type : types) {
                                                String selected = typeParam.equals(type[0]) ? "selected" : "";
                                    %>
                                                <option value="<%= type[0] %>" <%= selected %>><%= type[1] %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                        </div>

                        <div class="filter-row">
                            <div class="filter-item">
                                <label for="taille">Taille:</label>
                                <input type="text" id="taille" name="taille" placeholder="Entrez la taille" value="<%= tailleParam %>">
                            </div>

                            <div class="filter-item">
                                <label for="couleur">Couleur:</label>
                                <select id="couleur" name="couleur">
                                    <option value="">Toutes les couleurs</option>
                                    <%
                                        java.util.List<String[]> couleurs = (java.util.List<String[]>) request.getAttribute("couleurs");
                                        if(couleurs != null) {
                                            for(String[] couleur : couleurs) {
                                                String selected = couleurParam.equals(couleur[0]) ? "selected" : "";
                                    %>
                                                <option value="<%= couleur[0] %>" <%= selected %>><%= couleur[1] %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="filter-item">
                                <label>Prix:</label>
                                <div style="display: flex; gap: 5px;">
                                    <input type="number" name="prixMin" placeholder="Min" value="${param.prixMin != null ? param.prixMin : ''}" step="0.01">
                                    <input type="number" name="prixMax" placeholder="Max" value="${param.prixMax != null ? param.prixMax : ''}" step="0.01">
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="btn-add-to-cart"><i class="fas fa-filter"></i> Appliquer les filtres</button>
                        <a href="${pageContext.request.contextPath}/chaussures?action=view" class="btn-add-to-cart" style="background-color: #95a5a6; text-decoration: none; display: inline-block; padding: 8px 15px; margin-left: 10px;"><i class="fas fa-times"></i> Réinitialiser</a>
                    </form>
                </div>

                <!-- Résumé du panier -->
                <div class="cart-summary">
                    <%
                        com.example.model.Panier panier = (com.example.model.Panier) request.getAttribute("panier");
                        if(panier != null) {
                    %>
                    <h3>Panier (<%= panier.getItems() != null ? panier.getItems().size() : 0 %> articles) - Total: <%= panier.getTotal() %>€</h3>

                    <%
                        if(panier.getItems() != null && panier.getItems().size() > 0) {
                            for(com.example.model.PanierItem item : panier.getItems()) {
                    %>
                                <div class="cart-item">
                                    <div>
                                        <strong><%= item.getChaussure().getNom() %></strong><br>
                                        <small><%= item.getChaussure().getMarque() %> - Taille: <%= item.getChaussure().getTaille() %> - Couleur: <%= item.getChaussure().getCouleur() %></small><br>
                                        Quantité: <%= item.getQuantite() %> x <%= item.getPrixUnitaire() %>€ = <strong><%= item.getSousTotal() %>€</strong>
                                    </div>
                                    <div class="cart-actions">
                                        <form method="post" action="${pageContext.request.contextPath}/chaussures" style="display: inline;">
                                            <input type="hidden" name="action" value="updateCart">
                                            <input type="hidden" name="chaussureId" value="<%= item.getChaussure().getId() %>">
                                            <input type="number" name="quantity" value="<%= item.getQuantite() %>" min="1" max="10" style="width: 60px; padding: 3px;">
                                            <button type="submit" class="btn-cart-action update"><i class="fas fa-sync"></i></button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/chaussures" style="display: inline;">
                                            <input type="hidden" name="action" value="removeFromCart">
                                            <input type="hidden" name="chaussureId" value="<%= item.getChaussure().getId() %>">
                                            <button type="submit" class="btn-cart-action" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet article ?');"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </div>
                                </div>
                    <%
                            }
                    %>
                            <div style="margin-top: 15px; text-align: right;">
                                <a href="#" class="btn-add-to-cart" style="background-color: #2ecc71; text-decoration: none;"><i class="fas fa-lock"></i> Passer la commande</a>
                                <form method="post" action="${pageContext.request.contextPath}/chaussures" style="display: inline; margin-left: 10px;">
                                    <input type="hidden" name="action" value="clearCart">
                                    <button type="submit" class="btn-cart-action clear" onclick="return confirm('Êtes-vous sûr de vouloir vider le panier ?');"><i class="fas fa-trash-alt"></i> Vider le panier</button>
                                </form>
                            </div>
                    <%
                        } else {
                    %>
                            <p>Votre panier est vide.</p>
                    <%
                        }
                    } else {
                    %>
                        <p>Votre panier est vide.</p>
                    <%
                    }
                    %>
                </div>

                <!-- Liste des chaussures -->
                <div style="margin-top: 20px;">
                    <h3>Chaussures disponibles</h3>
                    <div class="product-grid">
                    <%
                        java.util.List<com.example.model.Chaussure> chaussures = (java.util.List<com.example.model.Chaussure>) request.getAttribute("chaussures");
                        if(chaussures != null && chaussures.size() > 0) {
                            for(com.example.model.Chaussure chaussure : chaussures) {
                    %>
                            <div class="product-card">
                                <%
                                    if(chaussure.getPhoto() != null && !chaussure.getPhoto().isEmpty()) {
                                %>
                                        <img src="${pageContext.request.contextPath}/images/<%= chaussure.getPhoto() %>" alt="<%= chaussure.getNom() %>" class="product-image">
                                <%
                                    } else {
                                %>
                                        <img src="${pageContext.request.contextPath}/images/placeholder_shoe.jpg" alt="<%= chaussure.getNom() %>" class="product-image">
                                <%
                                    }
                                %>

                                <h4><%= chaussure.getNom() %></h4>
                                <div class="product-price"><%= chaussure.getPrix() %> €</div>
                                <div class="product-stock">Stock: <%= chaussure.getStock() %></div>
                                <div>Marque: <%= chaussure.getMarque() %></div>
                                <div>Catégorie: <%= chaussure.getCategorie() %></div>
                                <div>Type: <%= chaussure.getType() %></div>
                                <div>Taille: <%= chaussure.getTaille() %></div>
                                <div>Couleur: <%= chaussure.getCouleur() %></div>
                                <p><%= chaussure.getDescription() %></p>

                                <form method="post" action="${pageContext.request.contextPath}/chaussures">
                                    <input type="hidden" name="action" value="addToCart">
                                    <input type="hidden" name="chaussureId" value="<%= chaussure.getId() %>">
                                    <label for="quantity_<%= chaussure.getId() %>">Quantité:</label>
                                    <input type="number" id="quantity_<%= chaussure.getId() %>" name="quantity" value="1" min="1" max="<%= chaussure.getStock() %>" style="width: 60px; padding: 5px; margin: 5px;">
                                    <button type="submit" class="btn-add-to-cart"><i class="fas fa-shopping-cart"></i> Ajouter au panier</button>
                                </form>
                            </div>
                    <%
                            }
                        } else {
                    %>
                        <p>Aucune chaussure trouvée avec les critères sélectionnés.</p>
                    <%
                        }
                    %>
                    </div>
                </div>
            </main>
        </div>
    </div>
</body>
</html>