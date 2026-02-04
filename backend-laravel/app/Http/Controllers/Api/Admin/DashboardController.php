<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Intervention;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;

class DashboardController extends Controller
{
    /**
     * Récupère les statistiques pour le tableau de bord.
     */
    public function getStats()
    {
        // Cette logique devrait idéalement être dans une classe de service (Service Layer)
        // pour garder le contrôleur léger.
        
        $completedThisMonth = Intervention::where('status', 'completed')
            ->whereMonth('completed_at', Carbon::now()->month)
            ->whereYear('completed_at', Carbon::now()->year)
            ->count();

        $pendingInterventions = Intervention::where('status', 'pending')->count();

        $monthlyRevenue = Intervention::where('status', 'completed')
            ->whereMonth('completed_at', Carbon::now()->month)
            ->whereYear('completed_at', Carbon::now()->year)
            ->sum('price');

        $stats = [
            'completed_this_month' => $completedThisMonth,
            'pending_interventions' => $pendingInterventions,
            'monthly_revenue' => number_format($monthlyRevenue, 2, '.', ''),
        ];

        return response()->json($stats);
    }
}