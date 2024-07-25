<?php

use App\Http\Controllers\EndpointController;
use Illuminate\Support\Facades\Route;

Route::get('/endpoint', [EndpointController::class, 'index'])->name('endpoint.index');
Route::post('/endpoint', [EndpointController::class, 'submit'])->name('endpoint.submit');
