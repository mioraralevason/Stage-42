-- Requêtes de recherche et de filtrage pour le site de vente de chaussures

-- 1. Recherche de chaussures avec filtres multiples
CREATE OR REPLACE FUNCTION rechercher_chaussures_filtrees(
    marque_param INTEGER DEFAULT NULL,
    categorie_param INTEGER DEFAULT NULL,
    type_chaussure_param INTEGER DEFAULT NULL,
    taille_param INTEGER DEFAULT NULL,
    couleur_param INTEGER DEFAULT NULL,
    prix_min_param DECIMAL(10,2) DEFAULT NULL,
    prix_max_param DECIMAL(10,2) DEFAULT NULL,
    stock_disponible BOOLEAN DEFAULT TRUE
)
RETURNS TABLE (
    id INTEGER,
    nom VARCHAR(200),
    prix DECIMAL(10,2),
    photo VARCHAR(255),
    description TEXT,
    stock INTEGER,
    marque VARCHAR(100),
    categorie VARCHAR(100),
    type_chaussure VARCHAR(100),
    taille VARCHAR(10),
    couleur VARCHAR(50)
)
LANGUAGE sql
AS $$
    SELECT 
        c.id,
        c.nom,
        c.prix,
        c.photo,
        c.description,
        c.stock,
        m.nom AS marque,
        cat.nom AS categorie,
        tc.nom AS type_chaussure,
        t.valeur AS taille,
        co.nom AS couleur
    FROM chaussures c
    LEFT JOIN marques m ON m.id = c.marque_id
    LEFT JOIN categories cat ON cat.id = c.categorie_id
    LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id
    LEFT JOIN tailles t ON t.id = c.taille_id
    LEFT JOIN couleurs co ON co.id = c.couleur_id
    WHERE 
        ($1 IS NULL OR c.marque_id = $1)
        AND ($2 IS NULL OR c.categorie_id = $2)
        AND ($3 IS NULL OR c.type_chaussure_id = $3)
        AND ($4 IS NULL OR c.taille_id = $4)
        AND ($5 IS NULL OR c.couleur_id = $5)
        AND ($6 IS NULL OR c.prix >= $6)
        AND ($7 IS NULL OR c.prix <= $7)
        AND ($8 = FALSE OR c.stock > 0)
    ORDER BY c.created_at DESC;
$$;

-- 2. Recherche de chaussures par nom ou description
CREATE OR REPLACE FUNCTION rechercher_chaussures_par_mot_cle(
    mot_cle VARCHAR(200)
)
RETURNS TABLE (
    id INTEGER,
    nom VARCHAR(200),
    prix DECIMAL(10,2),
    photo VARCHAR(255),
    description TEXT,
    stock INTEGER,
    marque VARCHAR(100),
    categorie VARCHAR(100)
)
LANGUAGE sql
AS $$
    SELECT 
        c.id,
        c.nom,
        c.prix,
        c.photo,
        c.description,
        c.stock,
        m.nom AS marque,
        cat.nom AS categorie
    FROM chaussures c
    LEFT JOIN marques m ON m.id = c.marque_id
    LEFT JOIN categories cat ON cat.id = c.categorie_id
    WHERE 
        c.nom ILIKE '%' || $1 || '%'
        OR c.description ILIKE '%' || $1 || '%'
        OR m.nom ILIKE '%' || $1 || '%'
    ORDER BY c.created_at DESC;
$$;

-- 3. Obtenir les meilleures ventes
CREATE OR REPLACE FUNCTION obtenir_meilleures_ventes(
    limite_param INTEGER DEFAULT 10
)
RETURNS TABLE (
    id INTEGER,
    nom VARCHAR(200),
    prix DECIMAL(10,2),
    photo VARCHAR(255),
    total_vendu INTEGER,
    marque VARCHAR(100)
)
LANGUAGE sql
AS $$
    SELECT 
        c.id,
        c.nom,
        c.prix,
        c.photo,
        COALESCE(SUM(ec.quantite), 0) AS total_vendu,
        m.nom AS marque
    FROM chaussures c
    LEFT JOIN elements_commande ec ON ec.chaussure_id = c.id
    LEFT JOIN marques m ON m.id = c.marque_id
    GROUP BY c.id, c.nom, c.prix, c.photo, m.nom
    ORDER BY total_vendu DESC, c.created_at DESC
    LIMIT $1;
$$;

-- 4. Obtenir les chaussures les plus récentes
CREATE OR REPLACE FUNCTION obtenir_nouveautes(
    limite_param INTEGER DEFAULT 10
)
RETURNS TABLE (
    id INTEGER,
    nom VARCHAR(200),
    prix DECIMAL(10,2),
    photo VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    marque VARCHAR(100)
)
LANGUAGE sql
AS $$
    SELECT 
        c.id,
        c.nom,
        c.prix,
        c.photo,
        c.description,
        c.created_at,
        m.nom AS marque
    FROM chaussures c
    LEFT JOIN marques m ON m.id = c.marque_id
    ORDER BY c.created_at DESC
    LIMIT $1;
$$;

-- 5. Filtrer par intervalle de prix
CREATE OR REPLACE FUNCTION filtrer_par_prix(
    prix_min DECIMAL(10,2),
    prix_max DECIMAL(10,2)
)
RETURNS TABLE (
    id INTEGER,
    nom VARCHAR(200),
    prix DECIMAL(10,2),
    photo VARCHAR(255),
    description TEXT,
    marque VARCHAR(100)
)
LANGUAGE sql
AS $$
    SELECT 
        c.id,
        c.nom,
        c.prix,
        c.photo,
        c.description,
        m.nom AS marque
    FROM chaussures c
    LEFT JOIN marques m ON m.id = c.marque_id
    WHERE c.prix BETWEEN $1 AND $2
    ORDER BY c.prix ASC;
$$;

-- 6. Obtenir statistiques sur les ventes
CREATE OR REPLACE FUNCTION obtenir_statistiques_ventes()
RETURNS TABLE (
    total_chaussures INTEGER,
    total_categories INTEGER,
    total_marques INTEGER,
    stock_total INTEGER,
    valeur_stock DECIMAL(12,2)
)
LANGUAGE sql
AS $$
    SELECT 
        COUNT(*) AS total_chaussures,
        COUNT(DISTINCT categorie_id) AS total_categories,
        COUNT(DISTINCT marque_id) AS total_marques,
        SUM(stock) AS stock_total,
        SUM(stock * prix) AS valeur_stock
    FROM chaussures;
$$;