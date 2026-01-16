<!-- Page similaire à remises.jsp mais pour les remises panier -->
<form method="post" action="${pageContext.request.contextPath}/chaussures">
    <input type="hidden" name="action" value="saveRemisePanier">
    
    <div class="form-group">
        <label>Articles minimum :</label>
        <input type="number" name="articlesMin" min="1" required>
    </div>
    
    <div class="form-group">
        <label>Taux de remise (%) :</label>
        <input type="number" name="tauxRemise" min="1" max="100" step="0.01" required>
    </div>
    
    <div class="form-group">
        <label>Description :</label>
        <input type="text" name="description" placeholder="Ex: Remise 20% pour 5+ articles">
    </div>
    
    <button type="submit">Ajouter la remise panier</button>
</form>