<?php
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\InterventionController;

Route::apiResource('interventions', InterventionController::class);