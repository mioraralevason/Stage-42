<%@ page import="java.util.List, com.example.model.Chaussure" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Chaussures - Application de Gestion de Chaussures</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .container {
            display: flex;
            min-height: 100vh;
            background-color: #f5f7fa;
        }

        .sidebar {
            width: 250px;
            background: linear-gradient(180deg, #2c3e50 0%, #1a252f 100%);
            color: white;
            position: fixed;
            height: 100vh;
            overflow-y: auto;
        }

        .sidebar-header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid #34495e;
        }

        .sidebar-header h2 {
            margin: 0;
            font-size: 1.2em;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .sidebar-menu {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .sidebar-menu li {
            margin: 0;
        }

        .sidebar-menu a {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: #ecf0f1;
            text-decoration: none;
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
        }

        .sidebar-menu a:hover {
            background-color: #34495e;
            color: #ffffff;
        }

        .sidebar-menu a.active {
            background-color: #3498db;
            border-left-color: #2980b9;
            color: white;
        }

        .sidebar-menu i {
            width: 25px;
            text-align: center;
            margin-right: 10px;
        }

        .main-content {
            flex: 1;
            margin-left: 250px;
            min-height: 100vh;
        }

        .header {
            background: white;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            margin: 0;
            color: #2c3e50;
            font-size: 1.8em;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .user-info img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #3498db;
        }

        .content-area {
            padding: 30px;
        }

        .content-header {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .content-header h2 {
            color: #2c3e50;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .content-header h2 i {
            color: #3498db;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 12px 25px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn-primary {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, #2980b9 0%, #3498db 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
        }

        .btn-success {
            background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
            color: white;
        }

        .btn-success:hover {
            background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(46, 204, 113, 0.3);
        }

        .card {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .data-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            margin-top: 20px;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        .data-table thead {
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
        }

        .data-table th {
            color: white;
            font-weight: 600;
            padding: 18px 15px;
            text-align: left;
            font-size: 0.95em;
            letter-spacing: 0.5px;
            border: none;
            position: relative;
        }

        .data-table th:not(:last-child)::after {
            content: '';
            position: absolute;
            right: 0;
            top: 25%;
            height: 50%;
            width: 1px;
            background-color: rgba(255, 255, 255, 0.1);
        }

        .data-table tbody tr {
            transition: all 0.3s ease;
            border-bottom: 1px solid #f1f1f1;
        }

        .data-table tbody tr:hover {
            background-color: #f8fafc;
            transform: translateY(-1px);
            box-shadow: 0 3px 8px rgba(0,0,0,0.1);
        }

        .data-table td {
            padding: 16px 15px;
            color: #2c3e50;
            border: none;
            font-size: 0.95em;
        }

        .data-table tbody tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .data-table tbody tr:nth-child(even):hover {
            background-color: #f1f7fd;
        }

        .badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
            text-align: center;
            min-width: 70px;
        }

        .badge-stock {
            background-color: #e8f6f3;
            color: #27ae60;
        }

        .badge-stock.low {
            background-color: #ffebee;
            color: #e74c3c;
        }

        .badge-price {
            background-color: #ebf5fb;
            color: #2980b9;
            font-weight: bold;
        }

        .actions-cell {
            display: flex;
            gap: 10px;
        }

        .btn-icon {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
            font-size: 0.9em;
        }

        .btn-edit {
            background-color: #f1c40f;
            color: white;
        }

        .btn-edit:hover {
            background-color: #f39c12;
            transform: translateY(-2px) scale(1.1);
        }

        .btn-delete {
            background-color: #e74c3c;
            color: white;
        }

        .btn-delete:hover {
            background-color: #c0392b;
            transform: translateY(-2px) scale(1.1);
        }

        .btn-view {
            background-color: #3498db;
            color: white;
        }

        .btn-view:hover {
            background-color: #2980b9;
            transform: translateY(-2px) scale(1.1);
        }

        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .summary-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
            display: flex;
            align-items: center;
            gap: 15px;
            transition: transform 0.3s ease;
        }

        .summary-card:hover {
            transform: translateY(-5px);
        }

        .summary-icon {
            width: 50px;
            height: 50px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5em;
        }

        .summary-icon.products {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
        }

        .summary-icon.stock {
            background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
            color: white;
        }

        .summary-icon.value {
            background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
            color: white;
        }

        .summary-info h3 {
            margin: 0;
            color: #7f8c8d;
            font-size: 0.9em;
            font-weight: 500;
        }

        .summary-info p {
            margin: 5px 0 0 0;
            color: #2c3e50;
            font-size: 1.5em;
            font-weight: bold;
        }

        .no-data {
            text-align: center;
            padding: 50px 20px;
            color: #7f8c8d;
        }

        .no-data i {
            font-size: 3em;
            color: #bdc3c7;
            margin-bottom: 20px;
        }

        .no-data h3 {
            color: #95a5a6;
            margin-bottom: 10px;
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 70px;
            }
            
            .sidebar-header h2 span,
            .sidebar-menu a span {
                display: none;
            }
            
            .main-content {
                margin-left: 70px;
            }
            
            .sidebar-header h2 i {
                margin-right: 0;
            }
            
            .sidebar-menu a {
                justify-content: center;
                padding: 15px;
            }
            
            .sidebar-menu i {
                margin-right: 0;
                font-size: 1.2em;
            }
            
            .content-header {
                flex-direction: column;
                gap: 15px;
                align-items: flex-start;
            }
            
            .data-table {
                display: block;
                overflow-x: auto;
            }
            
            .summary-cards {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Barre latérale -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <h2><i class="fas fa-store"></i> <span>Chaussure Shop</span></h2>
            </div>
            <ul class="sidebar-menu">
                <li><a href="${pageContext.request.contextPath}/home"><i class="fas fa-home"></i> <span>Accueil</span></a></li>
                <li><a href="${pageContext.request.contextPath}/chaussures"><i class="fas fa-shoe-prints"></i> <span>Vente Chaussures</span></a></li>
                <li><a href="${pageContext.request.contextPath}/chaussures?action=insert" class="active"><i class="fas fa-plus-circle"></i> <span>Ajouter</span></a></li>
                <li><a href="${pageContext.request.contextPath}/chaussures?action=listInserted"><i class="fas fa-list"></i> <span>Liste</span></a></li>
            </ul>
        </nav>

        <!-- Contenu principal -->
        <div class="main-content">
            <header class="header">
                <h1>Gestion des Chaussures</h1>
                <div class="user-info">
                    <img src="${pageContext.request.contextPath}/images/user-placeholder.png" alt="Utilisateur">
                    <span>Administrateur</span>
                </div>
            </header>

            <main class="content-area">
                <div class="content-header">
                    <h2><i class="fas fa-shoe-prints"></i> Liste des Chaussures</h2>
                    <a href="${pageContext.request.contextPath}/chaussures?action=insert" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Ajouter une chaussure
                    </a>
                </div>

                <% 
                    List<Chaussure> chaussures = (List<Chaussure>) request.getAttribute("chaussures");
                    if (chaussures != null && !chaussures.isEmpty()) {
                        // Calcul des statistiques
                        int totalChaussures = chaussures.size();
                        int totalStock = 0;
                        double valeurTotale = 0;
                        for (Chaussure c : chaussures) {
                            totalStock += c.getStock();
                            valeurTotale += c.getPrix() * c.getStock();
                        }
                %>

                <!-- Cartes de résumé -->
                <div class="summary-cards">
                    <div class="summary-card">
                        <div class="summary-icon products">
                            <i class="fas fa-shoe-prints"></i>
                        </div>
                        <div class="summary-info">
                            <h3>Total Chaussures</h3>
                            <p><%= totalChaussures %></p>
                        </div>
                    </div>
                    
                    <div class="summary-card">
                        <div class="summary-icon stock">
                            <i class="fas fa-boxes"></i>
                        </div>
                        <div class="summary-info">
                            <h3>Stock Total</h3>
                            <p><%= totalStock %></p>
                        </div>
                    </div>
                    
                    <div class="summary-card">
                        <div class="summary-icon value">
                            <i class="fas fa-euro-sign"></i>
                        </div>
                        <div class="summary-info">
                            <h3>Valeur Totale</h3>
                            <p><%= String.format("%.2f", valeurTotale) %> €</p>
                        </div>
                    </div>
                </div>

                <!-- Tableau des chaussures -->
                <div class="card">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nom</th>
                                <th>Marque</th>
                                <th>Catégorie</th>
                                <th>Taille</th>
                                <th>Couleur</th>
                                <th>Prix</th>
                                <th>Stock</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Chaussure c : chaussures) { 
                                String stockClass = c.getStock() < 10 ? "badge-stock low" : "badge-stock";
                            %>
                            <tr>
                                <td><strong>#<%= c.getId() %></strong></td>
                                <td><strong><%= c.getNom() %></strong></td>
                                <td><%= c.getMarque() %></td>
                                <td><%= c.getCategorie() %></td>
                                <td><%= c.getTaille() %></td>
                                <td>
                                    <div style="display: flex; align-items: center; gap: 8px;">
                                        <div style="width: 15px; height: 15px; border-radius: 50%; background-color: <%= c.getCouleur() %>; border: 1px solid #ddd;"></div>
                                        <span><%= c.getCouleur() %></span>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge badge-price">
                                        <%= String.format("%.2f", c.getPrix()) %> €
                                    </span>
                                </td>
                                <td>
                                    <span class="badge <%= stockClass %>">
                                        <i class="fas fa-box"></i> <%= c.getStock() %>
                                    </span>
                                </td>
                                <td class="actions-cell">
                                    <a href="#" class="btn-icon btn-view" title="Voir les détails">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="#" class="btn-icon btn-edit" title="Modifier">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <a href="#" class="btn-icon btn-delete" title="Supprimer" 
                                       onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette chaussure ?');">
                                        <i class="fas fa-trash"></i>
                                    </a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <% } else { %>
                
                <!-- Message quand aucune donnée -->
                <div class="card">
                    <div class="no-data">
                        <i class="fas fa-shoe-prints"></i>
                        <h3>Aucune chaussure trouvée</h3>
                        <p>Il n'y a pas encore de chaussures dans la base de données.</p>
                        <a href="${pageContext.request.contextPath}/chaussures?action=insert" class="btn btn-primary" style="margin-top: 20px;">
                            <i class="fas fa-plus"></i> Ajouter la première chaussure
                        </a>
                    </div>
                </div>
                
                <% } %>
            </main>
        </div>
    </div>

    <script>
        // Animation au chargement
        document.addEventListener('DOMContentLoaded', function() {
            // Animation des cartes de résumé
            const summaryCards = document.querySelectorAll('.summary-card');
            summaryCards.forEach((card, index) => {
                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });

            // Animation des lignes du tableau
            const tableRows = document.querySelectorAll('.data-table tbody tr');
            tableRows.forEach((row, index) => {
                setTimeout(() => {
                    row.style.opacity = '1';
                    row.style.transform = 'translateY(0)';
                }, index * 50);
            });

            // Confirmation pour la suppression
            const deleteButtons = document.querySelectorAll('.btn-delete');
            deleteButtons.forEach(button => {
                button.addEventListener('click', function(e) {
                    if (!confirm('Êtes-vous sûr de vouloir supprimer cette chaussure ? Cette action est irréversible.')) {
                        e.preventDefault();
                    }
                });
            });

            // Tri par colonne (fonctionnalité basique)
            const tableHeaders = document.querySelectorAll('.data-table th:not(:last-child)');
            tableHeaders.forEach(header => {
                header.style.cursor = 'pointer';
                header.addEventListener('click', function() {
                    const column = this.cellIndex;
                    this.classList.toggle('sorted');
                    
                    // Ici vous pourriez ajouter une logique de tri AJAX ou redirection
                    console.log('Tri par colonne:', column);
                });
            });
        });
    </script>
</body>
</html>