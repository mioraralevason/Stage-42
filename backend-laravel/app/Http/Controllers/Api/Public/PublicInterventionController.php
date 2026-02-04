<?php

namespace App\Http\Controllers\Api\Public;

use App\Http\Controllers\Controller;
use App\Http\Resources\InterventionResource;
use App\Models\Intervention;
use Illuminate\Http\Request;

class PublicInterventionController extends Controller
{
    /**
     * Affiche la liste des interventions en cours.
     * Accessible publiquement.
     */
    public function index()
    {
        // On ne charge que les véhicules, sans les infos clients, pour la partie publique.
        $interventions = Intervention::with(['vehicle'])
            ->where('status', 'in_progress')
            ->latest('updated_at')
            ->get();

        // On utilise une ressource qui expose moins de détails si nécessaire,
        // mais pour l'instant, InterventionResource fera l'affaire en cachant les relations non chargées.
        return InterventionResource::collection($interventions);
    }
}