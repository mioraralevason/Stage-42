<?php
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateInterventionsTable extends Migration
{
    public function up()
    {
        Schema::create('interventions', function (Blueprint $table) {
            $table->id();
            $table->string('name'); // frein, vidange, etc.
            $table->decimal('price', 8, 2);
            $table->integer('duration_seconds');
            $table->timestamps();
        });
    }
}