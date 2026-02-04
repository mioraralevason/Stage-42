<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Http\Requests\StoreInterventionRequest;
use App\Http\Requests\UpdateInterventionRequest;
use App\Http\Resources\InterventionResource;
use App\Models\Intervention;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class InterventionController extends Controller
{
    /**
     * Affiche une liste paginée des interventions.
     */
    public function index()
    {
        // Eager-load relations for performance (N+1 problem)
        $interventions = Intervention::with(['vehicle.client', 'user'])->latest()->paginate(15);
        
        return InterventionResource::collection($interventions);
    }

    /**
     * Crée une nouvelle intervention en base de données.
     */
    public function store(StoreInterventionRequest $request)
    {
        $intervention = Intervention::create($request->validated());
        
        return new InterventionResource($intervention->load(['vehicle', 'user']));
    }

    /**
     * Affiche une intervention spécifique.
     */
    public function show(Intervention $intervention)
    {
        $intervention->load(['vehicle.client', 'user']);
        
        return new InterventionResource($intervention);
    }

    /**
     * Met à jour une intervention.
     */
    public function update(UpdateInterventionRequest $request, Intervention $intervention)
    {
        $intervention->update($request->validated());

        // TODO: Déclencher l'événement pour Firebase si le statut a changé.
        // if ($intervention->wasChanged('status')) {
        //     event(new \App\Events\InterventionStatusUpdated($intervention));
        // }

        return new InterventionResource($intervention->load(['vehicle.client', 'user']));
    }

    /**
     * Supprime (soft delete) une intervention.
     */
    public function destroy(Intervention $intervention)
    {
        $intervention->delete();
        
        return response()->noContent(); // Standard REST response for successful deletion
    }
}