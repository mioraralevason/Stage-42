<?php
namespace App\Http\Controllers\Api;

use App\Models\Intervention;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class InterventionController extends Controller
{
    public function index()
    {
        $interventions = Intervention::all();
        return response()->json($interventions);
    }
    
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:100',
            'price' => 'required|numeric',
            'duration_seconds' => 'required|integer'
        ]);
        
        $intervention = Intervention::create($validated);
        
        return response()->json($intervention, 201);
    }
}