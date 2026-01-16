<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Insertion Chaussure - Application de Gestion de Chaussures</title>
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
            max-width: 800px;
            margin: 0 auto;
        }

        .form-container {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        .form-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .form-header h2 {
            color: #2c3e50;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .form-header p {
            color: #7f8c8d;
            margin-top: 5px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #2c3e50;
            font-size: 0.95em;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s ease;
            background-color: #fff;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
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

        .btn-secondary {
            background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
            color: white;
        }

        .btn-secondary:hover {
            background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(149, 165, 166, 0.3);
        }

        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }

        .input-icon {
            position: relative;
        }

        .input-icon i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #7f8c8d;
        }

        .input-icon input {
            padding-left: 45px;
        }

        .form-message {
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            display: none;
        }

        .form-message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
            display: block;
        }

        .form-message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            display: block;
        }

        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
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
                <li><a href="${pageContext.request.contextPath}/chaussures" class="active"><i class="fas fa-shoe-prints"></i> <span>Vente Chaussures</span></a></li>
               <li><a href="${pageContext.request.contextPath}/chaussures?action=insert">
        <i class="fas fa-plus-circle"></i> <span>Insertion Admin</span>
    </a></li></ul>
        </nav>

        <!-- Contenu principal -->
        <div class="main-content">
            <header class="header">
                <h1>Insertion Chaussure</h1>
                <div class="user-info">
                    <img src="${pageContext.request.contextPath}/images/user-placeholder.png" alt="Utilisateur">
                    <span>Administrateur</span>
                </div>
            </header>

            <main class="content-area">
                <div class="form-container">
                    <div class="form-header">
                        <h2><i class="fas fa-plus-circle"></i> Ajouter une nouvelle chaussure</h2>
                        <p>Remplissez le formulaire ci-dessous pour ajouter un nouveau produit</p>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/chaussures">
                        <input type="hidden" name="action" value="save">
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="nom"><i class="fas fa-tag"></i> Nom:</label>
                                <input type="text" id="nom" name="nom" required placeholder="Entrez le nom de la chaussure">
                            </div>
                            
                            <div class="form-group">
                                <label for="prix"><i class="fas fa-euro-sign"></i> Prix:</label>
                                <input type="number" id="prix" name="prix" step="0.01" required placeholder="0.00">
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="stock"><i class="fas fa-boxes"></i> Stock:</label>
                                <input type="number" id="stock" name="stock" required placeholder="Quantité disponible">
                            </div>
                            
                            <div class="form-group">
                                <label for="marqueId"><i class="fas fa-copyright"></i> Marque:</label>
                                <select id="marqueId" name="marqueId">
                                    <% for(String[] marque : (List<String[]>)request.getAttribute("marques")) { %>
                                        <option value="<%= marque[0] %>"><%= marque[1] %></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="categorieId"><i class="fas fa-list"></i> Catégorie:</label>
                                <select id="categorieId" name="categorieId">
                                    <% for(String[] cat : (List<String[]>)request.getAttribute("categories")) { %>
                                        <option value="<%= cat[0] %>"><%= cat[1] %></option>
                                    <% } %>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="tailleId"><i class="fas fa-ruler"></i> Taille:</label>
                                <select id="tailleId" name="tailleId">
                                    <% for(String[] taille : (List<String[]>)request.getAttribute("tailles")) { %>
                                        <option value="<%= taille[0] %>"><%= taille[1] %></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="couleurId"><i class="fas fa-palette"></i> Couleur:</label>
                                <select id="couleurId" name="couleurId">
                                    <% for(String[] couleur : (List<String[]>)request.getAttribute("couleurs")) { %>
                                        <option value="<%= couleur[0] %>"><%= couleur[1] %></option>
                                    <% } %>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="description"><i class="fas fa-align-left"></i> Description:</label>
                                <textarea id="description" name="description" rows="3" placeholder="Description optionnelle..."></textarea>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/chaussures?action=listInserted" class="btn btn-secondary">
                                <i class="fas fa-list"></i> Voir la liste
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Insérer la chaussure
                            </button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    </div>

    <script>
        // Script pour améliorer l'expérience utilisateur
        document.addEventListener('DOMContentLoaded', function() {
            // Animation des champs de formulaire
            const inputs = document.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                input.addEventListener('focus', function() {
                    this.parentElement.style.transform = 'translateY(-2px)';
                });
                
                input.addEventListener('blur', function() {
                    this.parentElement.style.transform = 'translateY(0)';
                });
            });

            // Validation en temps réel
            const form = document.querySelector('form');
            form.addEventListener('submit', function(e) {
                let isValid = true;
                const requiredFields = form.querySelectorAll('[required]');
                
                requiredFields.forEach(field => {
                    if (!field.value.trim()) {
                        isValid = false;
                        field.style.borderColor = '#e74c3c';
                    } else {
                        field.style.borderColor = '#2ecc71';
                    }
                });
                
                if (!isValid) {
                    e.preventDefault();
                    alert('Veuillez remplir tous les champs obligatoires.');
                }
            });

            // Effet de transition au chargement
            setTimeout(() => {
                document.querySelector('.form-container').style.opacity = '1';
                document.querySelector('.form-container').style.transform = 'translateY(0)';
            }, 100);
        });
    </script>
</body>
</html>