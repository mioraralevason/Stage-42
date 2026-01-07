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
                <li><a href="${pageContext.request.contextPath}/home" class="active"><i class="fas fa-home"></i> <span>Accueil</span></a></li>
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
                <p>${message}</p>
                
                <div style="margin-top: 20px;">
                    <h3>Fonctionnalités disponibles</h3>
                    <ul>
                        <li>Gestion des produits</li>
                        <li>Gestion des clients</li>
                        <li>Suivi des commandes</li>
                        <li>Statistiques de vente</li>
                    </ul>
                </div>
            </main>
        </div>
    </div>
</body>
</html>