-- Opérations CRUD pour le panier

-- 1. Ajouter un article au panier
CREATE OR REPLACE FUNCTION ajouter_au_panier(
    utilisateur_id_param INTEGER,
    chaussure_id_param INTEGER,
    quantite_param INTEGER
)
RETURNS TABLE (
    success BOOLEAN,
    message TEXT,
    element_id INTEGER,
    quantite_finale INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    panier_id_local INTEGER;
    chaussure_stock INTEGER;
    quantite_existante INTEGER := 0;
    quantite_finale_local INTEGER;
BEGIN
    -- Récupérer l'ID du panier de l'utilisateur
    SELECT id INTO panier_id_local
    FROM paniers
    WHERE utilisateur_id = utilisateur_id_param;

    -- Si aucun panier n'existe, en créer un nouveau
    IF NOT FOUND THEN
        INSERT INTO paniers (utilisateur_id)
        VALUES (utilisateur_id_param)
        RETURNING id INTO panier_id_local;
    END IF;

    -- Vérifier le stock disponible pour la chaussure
    SELECT stock INTO chaussure_stock
    FROM chaussures
    WHERE id = chaussure_id_param;

    -- Vérifier si la quantité demandée est disponible en stock
    IF chaussure_stock < quantite_param THEN
        RETURN QUERY SELECT FALSE, 'Quantité demandée indisponible en stock.', NULL::INTEGER, NULL::INTEGER;
        RETURN;
    END IF;

    -- Vérifier si la chaussure est déjà dans le panier
    SELECT quantite INTO quantite_existante
    FROM elements_panier
    WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;

    IF FOUND THEN
        -- Mise à jour de la quantité si elle est inférieure ou égale au stock
        quantite_finale_local := quantite_existante + quantite_param;
        IF chaussure_stock >= quantite_finale_local THEN
            UPDATE elements_panier
            SET quantite = quantite_finale_local,
                updated_at = CURRENT_TIMESTAMP
            WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;

            RETURN QUERY SELECT TRUE, 'Quantité mise à jour dans le panier avec succès.', id, quantite_finale_local
            FROM elements_panier
            WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;
        ELSE
            RETURN QUERY SELECT FALSE, 'Quantité totale demandée indisponible en stock.', NULL::INTEGER, NULL::INTEGER;
            RETURN;
        END IF;
    ELSE
        -- Ajouter la chaussure au panier
        INSERT INTO elements_panier (panier_id, chaussure_id, quantite, prix_unitaire)
        SELECT panier_id_local, chaussure_id_param, quantite_param, prix
        FROM chaussures
        WHERE id = chaussure_id_param;

        RETURN QUERY SELECT TRUE, 'Article ajouté au panier avec succès.', id, quantite_param
        FROM elements_panier
        WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;
    END IF;

END;
$$;

-- 2. Supprimer un article du panier
CREATE OR REPLACE FUNCTION supprimer_du_panier(
    utilisateur_id_param INTEGER,
    chaussure_id_param INTEGER
)
RETURNS TABLE (
    success BOOLEAN,
    message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    panier_id_local INTEGER;
BEGIN
    -- Récupérer l'ID du panier de l'utilisateur
    SELECT id INTO panier_id_local
    FROM paniers
    WHERE utilisateur_id = utilisateur_id_param;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, 'Panier non trouvé.';
        RETURN;
    END IF;

    -- Supprimer l'article du panier
    DELETE FROM elements_panier
    WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;

    IF FOUND THEN
        RETURN QUERY SELECT TRUE, 'Article supprimé du panier avec succès.';
    ELSE
        RETURN QUERY SELECT FALSE, 'Article non trouvé dans le panier.';
    END IF;

END;
$$;

-- 3. Mettre à jour la quantité d'un article dans le panier
CREATE OR REPLACE FUNCTION mettre_a_jour_quantite_panier(
    utilisateur_id_param INTEGER,
    chaussure_id_param INTEGER,
    nouvelle_quantite_param INTEGER
)
RETURNS TABLE (
    success BOOLEAN,
    message TEXT,
    quantite_finale INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    panier_id_local INTEGER;
    chaussure_stock INTEGER;
    ancienne_quantite INTEGER;
BEGIN
    -- Récupérer l'ID du panier de l'utilisateur
    SELECT id INTO panier_id_local
    FROM paniers
    WHERE utilisateur_id = utilisateur_id_param;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, 'Panier non trouvé.', NULL::INTEGER;
        RETURN;
    END IF;

    -- Obtenir le stock disponible et l'ancienne quantité
    SELECT c.stock, ep.quantite INTO chaussure_stock, ancienne_quantite
    FROM elements_panier ep
    JOIN chaussures c ON c.id = ep.chaussure_id
    WHERE ep.panier_id = panier_id_local AND ep.chaussure_id = chaussure_id_param;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, 'Article non trouvé dans le panier.', NULL::INTEGER;
        RETURN;
    END IF;

    -- Vérifier si la nouvelle quantité est disponible en stock
    IF chaussure_stock < nouvelle_quantite_param THEN
        RETURN QUERY SELECT FALSE, 'Quantité demandée indisponible en stock.', NULL::INTEGER;
        RETURN;
    END IF;

    -- Mettre à jour la quantité dans le panier
    UPDATE elements_panier
    SET quantite = nouvelle_quantite_param,
        updated_at = CURRENT_TIMESTAMP
    WHERE panier_id = panier_id_local AND chaussure_id = chaussure_id_param;

    RETURN QUERY SELECT TRUE, 'Quantité mise à jour avec succès.', nouvelle_quantite_param;

END;
$$;

-- 4. Afficher le contenu du panier
CREATE OR REPLACE FUNCTION obtenir_contenu_panier(
    utilisateur_id_param INTEGER
)
RETURNS TABLE (
    id INTEGER,
    chaussure_nom VARCHAR(200),
    taille_valeur VARCHAR(10),
    couleur_nom VARCHAR(50),
    marque_nom VARCHAR(100),
    prix_unitaire DECIMAL(10,2),
    quantite INTEGER,
    sous_total DECIMAL(10,2),
    photo VARCHAR(255)
)
LANGUAGE sql
AS $$
    SELECT 
        ep.id,
        c.nom AS chaussure_nom,
        t.valeur AS taille_valeur,
        co.nom AS couleur_nom,
        m.nom AS marque_nom,
        ep.prix_unitaire,
        ep.quantite,
        (ep.quantite * ep.prix_unitaire) AS sous_total,
        c.photo
    FROM elements_panier ep
    JOIN paniers p ON p.id = ep.panier_id
    JOIN chaussures c ON c.id = ep.chaussure_id
    LEFT JOIN tailles t ON t.id = c.taille_id
    LEFT JOIN couleurs co ON co.id = c.couleur_id
    LEFT JOIN marques m ON m.id = c.marque_id
    WHERE p.utilisateur_id = utilisateur_id_param
    ORDER BY ep.created_at DESC;
$$;

-- 5. Effacer le panier
CREATE OR REPLACE FUNCTION vider_panier(
    utilisateur_id_param INTEGER
)
RETURNS TABLE (
    success BOOLEAN,
    message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    panier_id_local INTEGER;
BEGIN
    -- Récupérer l'ID du panier de l'utilisateur
    SELECT id INTO panier_id_local
    FROM paniers
    WHERE utilisateur_id = utilisateur_id_param;

    IF NOT FOUND THEN
        RETURN QUERY SELECT FALSE, 'Panier non trouvé.';
        RETURN;
    END IF;

    -- Supprimer tous les éléments du panier
    DELETE FROM elements_panier
    WHERE panier_id = panier_id_local;

    RETURN QUERY SELECT TRUE, 'Panier vidé avec succès.';

END;
$$;