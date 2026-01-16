-- Base de données pour l'application de vente de chaussures
-- Création des tables avec clés étrangères

-- Table des marques de chaussures
CREATE TABLE IF NOT EXISTS marques (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table des catégories de chaussures
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table des types de chaussures
CREATE TABLE IF NOT EXISTS types_chaussures (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table des tailles disponibles
CREATE TABLE IF NOT EXISTS tailles (
    id SERIAL PRIMARY KEY,
    valeur VARCHAR(10) NOT NULL UNIQUE -- Ex: 36, 37, 38, etc.
);

-- Table des couleurs disponibles
CREATE TABLE IF NOT EXISTS couleurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    code_hex VARCHAR(7) -- Ex: #FF0000 pour rouge
);

-- Table des chaussures
CREATE TABLE IF NOT EXISTS chaussures (
    id SERIAL PRIMARY KEY,
    photo VARCHAR(255), -- Chemin vers l'image
    nom VARCHAR(200) NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    taille_id INTEGER REFERENCES tailles(id),
    couleur_id INTEGER REFERENCES couleurs(id),
    description TEXT,
    marque_id INTEGER REFERENCES marques(id),
    categorie_id INTEGER REFERENCES categories(id),
    type_chaussure_id INTEGER REFERENCES types_chaussures(id),
    stock INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des utilisateurs (clients)
CREATE TABLE IF NOT EXISTS utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    ville VARCHAR(100),
    code_postal VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des paniers
CREATE TABLE IF NOT EXISTS paniers (
    id SERIAL PRIMARY KEY,
    utilisateur_id INTEGER REFERENCES utilisateurs(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(utilisateur_id) -- Un utilisateur ne peut avoir qu'un seul panier actif
);

-- Table des éléments du panier
CREATE TABLE IF NOT EXISTS elements_panier (
    id SERIAL PRIMARY KEY,
    panier_id INTEGER REFERENCES paniers(id) ON DELETE CASCADE,
    chaussure_id INTEGER REFERENCES chaussures(id) ON DELETE CASCADE,
    quantite INTEGER NOT NULL DEFAULT 1,
    prix_unitaire DECIMAL(10,2) NOT NULL, -- Prix au moment de l'ajout au panier
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(panier_id, chaussure_id) -- Une chaussure ne peut être qu'une fois dans un panier
);

-- Table des commandes
CREATE TABLE IF NOT EXISTS commandes (
    id SERIAL PRIMARY KEY,
    utilisateur_id INTEGER REFERENCES utilisateurs(id),
    numero_commande VARCHAR(50) UNIQUE NOT NULL,
    statut VARCHAR(50) DEFAULT 'en_attente', -- en_attente, confirmee, expediée, livrée, annulee
    sous_total DECIMAL(10,2) NOT NULL,
    frais_livraison DECIMAL(10,2) DEFAULT 0,
    taxe DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    adresse_facturation TEXT,
    adresse_livraison TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des éléments de commande
CREATE TABLE IF NOT EXISTS elements_commande (
    id SERIAL PRIMARY KEY,
    commande_id INTEGER REFERENCES commandes(id) ON DELETE CASCADE,
    chaussure_id INTEGER REFERENCES chaussures(id),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL
);
-- Table des remises
CREATE TABLE IF NOT EXISTS remise (
    id SERIAL PRIMARY KEY,
    chaussure_id INTEGER NOT NULL REFERENCES chaussures(id) ON DELETE CASCADE,  -- CHANGER ICI
    quantite_min INTEGER NOT NULL DEFAULT 2,
    taux_remise DECIMAL(5,2) NOT NULL
);



CREATE TABLE IF NOT EXISTS remise_panier (
    id SERIAL PRIMARY KEY,
    articles_min INTEGER NOT NULL,  -- Nombre minimum d'articles dans le panier
    taux_remise DECIMAL(5,2) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insérer des remises panier
INSERT INTO remise_panier (articles_min, taux_remise, description) VALUES
(5, 20.00, 'Remise 20% pour 5+ articles'),
(5, 20.00, 'Remise 30% pour 10+ articles'),
(5, 40.00, 'Remise 40% pour 20+ articles');


-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_chaussures_marque ON chaussures(marque_id);
CREATE INDEX IF NOT EXISTS idx_chaussures_categorie ON chaussures(categorie_id);
CREATE INDEX IF NOT EXISTS idx_chaussures_type ON chaussures(type_chaussure_id);
CREATE INDEX IF NOT EXISTS idx_chaussures_taille ON chaussures(taille_id);
CREATE INDEX IF NOT EXISTS idx_chaussures_couleur ON chaussures(couleur_id);
CREATE INDEX IF NOT EXISTS idx_elements_panier_panier ON elements_panier(panier_id);
CREATE INDEX IF NOT EXISTS idx_elements_panier_chaussure ON elements_panier(chaussure_id);
CREATE INDEX IF NOT EXISTS idx_commandes_utilisateur ON commandes(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_commandes_numero ON commandes(numero_commande);

-- Insertion de données de démonstration
INSERT INTO marques (nom, description) VALUES 
('Nike', 'Marque américaine de sport'),
('Adidas', 'Marque allemande de sport'),
('Puma', 'Marque allemande de sport'),
('Timberland', 'Spécialiste des bottes de travail et de mode'),
('Converse', 'Connue pour ses baskets Chuck Taylor');

INSERT INTO categories (nom, description) VALUES 
('Sport', 'Chaussures de sport et de loisirs'),
('Mode', 'Chaussures de mode et casual'),
('Travail', 'Chaussures professionnelles'),
('Enfant', 'Chaussures pour enfants'),
('Running', 'Chaussures de course à pied');

INSERT INTO types_chaussures (nom, description) VALUES 
('Baskets', 'Chaussures de sport légères'),
('Bottes', 'Chaussures hautes'),
('Chaussures basses', 'Chaussures sans tige haute'),
('Sandales', 'Chaussures ouvertes'),
('Escarpins', 'Chaussures à talons pour femmes'),
('Mocassins', 'Chaussures souples sans lacets');

INSERT INTO tailles (valeur) VALUES 
('35'), ('36'), ('37'), ('38'), ('39'), ('40'), ('41'), ('42'), ('43'), ('44'), ('45'), ('46');

INSERT INTO couleurs (nom, code_hex) VALUES 
('Noir', '#000000'),
('Blanc', '#FFFFFF'),
('Rouge', '#FF0000'),
('Bleu', '#0000FF'),
('Vert', '#00FF00'),
('Jaune', '#FFFF00'),
('Rose', '#FFC0CB'),
('Gris', '#808080'),
('Marron', '#A52A2A'),
('Orange', '#FFA500');

-- Insertion de chaussures de démonstration
INSERT INTO chaussures (nom, prix, photo, description, marque_id, categorie_id, type_chaussure_id, taille_id, couleur_id, stock) VALUES 
('Nike Air Max 270', 129.99, 'nike_air_max.jpg', 'Basket de course confortable avec amorti Air Max', 1, 1, 1, 6, 1, 10),
('Adidas Ultraboost', 149.99, 'adidas_ultraboost.jpg', 'Basket de running haut de gamme', 2, 5, 1, 7, 2, 15),
('Timberland Classic', 199.99, 'timberland_classic.jpg', 'Bottes emblématiques résistantes', 4, 3, 2, 8, 3, 5),
('Converse Chuck Taylor', 59.99, 'converse_chuck_taylor.jpg', 'Baskets iconiques en toile', 5, 2, 1, 5, 2, 20);
