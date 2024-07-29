<?php

declare(strict_types=1);

namespace App\Http\Controllers;

use App\Http\Requests\EndpointRequest;
use App\Services\ContactService;
use App\Services\LeadService;
use App\Services\TaskService;
use Illuminate\Http\JsonResponse;
use Illuminate\Routing\Controller;
use Illuminate\View\View;
use Symfony\Component\HttpFoundation\Response;

final class EndpointController extends Controller
{
    public function __construct(
        private readonly ContactService $contactService,
        private readonly LeadService $leadService,
        private readonly TaskService $taskService
    ) {}

    public function index(): View
    {
        return view('endpoint');
    }

    public function submit(EndpointRequest $request): JsonResponse
    {
        $responseData = [];

        $contactResult = $this->contactService->submitContact([
            'email'      => $request->email,
            'phone'      => $request->phone,
            'gender'     => $request->gender,
            'first_name' => $request->first_name,
            'last_name'  => $request->last_name,
        ]);

        $responseData['contactResult'] = $contactResult;

        if ($contactResult->getContactModel() === null) {
            return response()
                ->json(['error' => 'Произошла ошибка при создании контакта'])
                ->setStatusCode(Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        if ($contactResult->shouldCreateLead()) {
            $lead = $this->leadService->submitLead($contactResult->getContactModel());

            if ($lead === null) {
                return response()
                    ->json(['error' => 'Произошла ошибка при создании сделки'])
                    ->setStatusCode(Response::HTTP_INTERNAL_SERVER_ERROR);
            }

            $task = $this->taskService->submitTask($lead);

            if ($task === null) {
                return response()
                    ->json(['error' => 'Произошла ошибка при создании задачи'])
                    ->setStatusCode(Response::HTTP_INTERNAL_SERVER_ERROR);
            }

            $responseData['lead'] = $lead->toArray();
            $responseData['task'] = $task->toArray();
        }

        $responseData['success'] = 'Успешно добавлено';

        return response()
            ->json($responseData)
            ->setStatusCode(Response::HTTP_OK);
    }
}
