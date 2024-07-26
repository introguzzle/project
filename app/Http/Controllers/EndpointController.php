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
    private ContactService $contactService;
    private LeadService $leadService;
    private TaskService $taskService;

    public function __construct(
        ContactService $contactService,
        LeadService $leadService,
        TaskService $taskService
    )
    {
        $this->contactService = $contactService;
        $this->leadService = $leadService;
        $this->taskService = $taskService;
    }

    public function index(): View
    {
        return view('endpoint');
    }

    public function submit(EndpointRequest $request): JsonResponse
    {
        $flush = [];

        $contactResult = $this->contactService->submitContact([
            'email'      => $request->email,
            'phone'      => $request->phone,
            'gender'     => $request->gender,
            'first_name' => $request->first_name,
            'last_name'  => $request->last_name,
        ]);

        $flush['contactResult'] = $contactResult;

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

            $flush['lead'] = $lead->toArray();
            $flush['task'] = $task->toArray();
        }

        $flush['success'] = 'Успешно добавлено';

        return response()
            ->json($flush)
            ->setStatusCode(Response::HTTP_OK);
    }
}
