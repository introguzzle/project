<?php
declare(strict_types=1);

namespace App\Http\Controllers;

use AmoCRM\Client\AmoCRMApiClient;
use App\Http\Requests\EndpointRequest;

use App\Services\ContactService;

use App\Services\LeadService;
use App\Services\TaskService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Routing\Controller;
use Illuminate\View\View;

final class EndpointController extends Controller
{
    private ContactService $contactService;
    private LeadService $leadService;
    private TaskService $taskService;
    private AmoCRMApiClient $client;

    public function __construct(
        AmoCRMApiClient $client,
        ContactService $contactService,
        LeadService $leadService,
        TaskService $taskService
    )
    {
        $this->client = $client;
        $this->contactService = $contactService;
        $this->leadService = $leadService;
        $this->taskService = $taskService;
    }

    public function index(): View
    {
        return view('endpoint');
    }

    public function submit(EndpointRequest $request): RedirectResponse
    {
        $response = redirect()->route('endpoint.index');

        $contact = $this->contactService->submitContact([
            'email'      => $request->email,
            'phone'      => $request->phone,
            'gender'     => $request->gender,
            'first_name' => $request->first_name,
            'last_name'  => $request->last_name,
        ]);

        if ($contact === null) {
            return $response->with(['error' => 'Произошла ошибка при создании контакта']);
        }

        $lead = $this->leadService->submitLead($contact);

        if ($lead === null) {
            return $response->with(['error' => 'Произошла ошибка при создании сделки']);
        }

        $task = $this->taskService->submitTask($lead);
        if ($task === null) {
            return $response->with(['error' => 'Произошла ошибка при создании задачи']);
        }


        return $response->with(['success' => 'Успешно добавлено']);
    }
}
