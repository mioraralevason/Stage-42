# Base de données pour le site de vente de chaussures

Ce dossier contient les scripts SQL nécessaires pour créer et gérer la base de données de l'application de vente de chaussures.

## Structure des fichiers

- `schema.sql` - Crée la structure de base de données (tables, relations, données initiales)
- `operations_panier.sql` - Fonctions pour gérer les opérations de panier (ajouter, supprimer, modifier, etc.)
- `requetes_filtres.sql` - Fonctions pour la recherche et les filtres (par marque, catégorie, prix, etc.)

## Installation

### 1. Créer la base de données

Connectez-vous à PostgreSQL en tant que superutilisateur et exécutez :

```sql
CREATE DATABASE chaussure_db;
GRANT ALL PRIVILEGES ON DATABASE chaussure_db TO postgres;
\c chaussure_db
```

### 2. Exécuter le script de schéma

```bash
psql -d chaussure_db -U postgres -f schema.sql
```

### 3. Exécuter les scripts d'opérations

```bash
psql -d chaussure_db -U postgres -f operations_panier.sql
psql -d chaussure_db -U postgres -f requetes_filtres.sql
```

## Tables principales

1. **chaussures** - Informations sur les chaussures (nom, prix, photo, taille, couleur, etc.)
2. **marques** - Marques des chaussures
3. **categories** - Catégories de chaussures
4. **types_chaussures** - Types de chaussures (baskets, bottes, etc.)
5. **tailles** - Tailles disponibles
6. **couleurs** - Couleurs disponibles
7. **utilisateurs** - Informations clients
8. **paniers** - Paniers des utilisateurs
9. **elements_panier** - Articles dans les paniers

## Fonctions utiles

### Opérations panier

- `ajouter_au_panier(utilisateur_id, chaussure_id, quantite)` - Ajouter un article au panier
- `supprimer_du_panier(utilisateur_id, chaussure_id)` - Supprimer un article du panier
- `mettre_a_jour_quantite_panier(utilisateur_id, chaussure_id, nouvelle_quantite)` - Modifier la quantité
- `obtenir_contenu_panier(utilisateur_id)` - Afficher le contenu du panier
- `vider_panier(utilisateur_id)` - Supprimer tous les articles du panier

### Recherche et filtres

- `rechercher_chaussures_filtrees(...)` - Recherche avec plusieurs filtres
- `rechercher_chaussures_par_mot_cle(keyword)` - Recherche par mot-clé
- `obtenir_meilleures_ventes(limite)` - Chaussures les plus vendues
- `obtenir_nouveautes(limite)` - Chaussures récemment ajoutées
- `filtrer_par_prix(prix_min, prix_max)` - Filtrer par intervalle de prix
- `obtenir_statistiques_ventes()` - Statistiques générales

## Exemples d'utilisation

### Ajouter un article au panier
```sql
SELECT * FROM ajouter_au_panier(1, 1, 2);
```

### Rechercher avec filtres
```sql
SELECT * FROM rechercher_chaussures_filtrees(
    marque_param => 1,
    categorie_param => 2,
    prix_min_param => 50.00,
    prix_max_param => 200.00
);
```

### Obtenir le contenu du panier
```sql
SELECT * FROM obtenir_contenu_panier(1);
```

## Relations

- Une chaussure appartient à une marque, une catégorie, un type, une taille et une couleur
- Un utilisateur peut avoir un panier
- Un panier peut contenir plusieurs articles (éléments_panier)
- Un élément de panier référence une chaussure spécifique