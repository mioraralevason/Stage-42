<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                <h2><i class="fas fa-store"></i> Multi-Activités Pro</h2>
            </div>
            <ul class="sidebar-menu">
                <li><a href="${pageContext.request.contextPath}/home"><i class="fas fa-home"></i> <span>Accueil</span></a></li>
                <li><a href="${pageContext.request.contextPath}/chaussures"><i class="fas fa-shoe-prints"></i> <span>Vente Chaussures</span></a></li>
                <li><a href="#"><i class="fas fa-glass-martini-alt"></i> <span>Location Vaisselle</span></a></li>
                <li><a href="#"><i class="fas fa-tshirt"></i> <span>Vente Tissu</span></a></li>
                <li><a href="#"><i class="fas fa-users"></i> <span>Clients</span></a></li>
                <li><a href="#"><i class="fas fa-shopping-cart"></i> <span>Commandes</span></a></li>
                <li><a href="#"><i class="fas fa-chart-bar"></i> <span>Statistiques</span></a></li>
                <li><a href="#"><i class="fas fa-cog"></i> <span>Paramètres</span></a></li>
                <li><a href="#"><i class="fas fa-question-circle"></i> <span>Aide</span></a></li>
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
</html>