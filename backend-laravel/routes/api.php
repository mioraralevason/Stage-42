<?php

use App\Http\Controllers\Api\Admin\DashboardController;
use App\Http\Controllers\Api\Admin\InterventionController;
use App\Http\Controllers\Api\Public\PublicInterventionController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Ici, vous pouvez enregistrer les routes API pour votre application.
| Ces routes sont chargées par le RouteServiceProvider et sont toutes
| assignées au groupe de middleware "api".
|
*/

// --- Routes publiques (Frontoffice) ---
// Pas d'authentification requise.
Route::prefix('v1/public')->name('api.public.')->group(function () {
    Route::get('/interventions-en-cours', [PublicInterventionController::class, 'index'])->name('interventions.index');
});


// --- Routes d'authentification (à implémenter avec un AuthController dédié) ---
// POST /api/login
// POST /api/logout


// --- Routes sécurisées (Backoffice) ---
// Nécessite un token Sanctum valide.
Route::prefix('v1/admin')->name('api.admin.')->middleware(['auth:sanctum'])->group(function () {

    // Dashboard
    // GET /api/v1/admin/dashboard/stats
    Route::get('/dashboard/stats', [DashboardController::class, 'getStats'])->name('dashboard.stats');

    // CRUD complet pour les interventions
    // Accessible par les 'admin' et 'mechanic'
    Route::apiResource('interventions', InterventionController::class)->middleware('role:admin,mechanic');

    // --- Routes réservées aux administrateurs ---
    Route::middleware(['role:admin'])->group(function() {
        // Ici, vous ajouteriez les routes pour gérer les utilisateurs, etc.
        // Exemple: Route::apiResource('users', UserController::class);
    });
});


// Route par défaut de Laravel pour récupérer l'utilisateur authentifié via le token
Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
