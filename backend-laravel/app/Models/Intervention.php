<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Intervention extends Model
{
    protected $fillable = [
        'name', 
        'price', 
        'duration_seconds',
        'description'
    ];
    
    public function reparations()
    {
        return $this->hasMany(Reparation::class);
    }
}