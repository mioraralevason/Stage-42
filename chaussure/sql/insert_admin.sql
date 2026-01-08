-- Script pour créer l'utilisateur administrateur de test
-- Insérer un utilisateur administrateur dans la base de données

-- Insertion d'un utilisateur administrateur de test
INSERT INTO utilisateurs (nom, email, mot_de_passe, telephone, adresse, ville, code_postal, created_at)
VALUES ('Administrateur', 'admin@gmail.com', '123456', '+261 34 123 45 67', 'Adresse Admin', 'Antananarivo', '101',
        NOW())
ON CONFLICT (email) DO NOTHING;

-- Si l'utilisateur existe déjà, on le met à jour
UPDATE utilisateurs 
SET mot_de_passe = '123456', nom = 'Administrateur'
WHERE email = 'admin@gmail.com';