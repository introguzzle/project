<?php

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Models\LeadModel;
use AmoCRM\Models\TaskModel;
use Carbon\Carbon;
use Carbon\CarbonInterface;
use Throwable;

final class TaskService
{
    private AmoCRMApiClient $client;

    public function __construct(AmoCRMApiClient $client)
    {
        $this->client = $client;
    }

    /**
     * Создает задачу и привязывает её к сделку
     *
     * @param LeadModel $lead
     * @return TaskModel|null
     */
    public function submitTask(LeadModel $lead): ?TaskModel
    {
        $task = (new TaskModel())
            ->setAccountId($lead->getAccountId())
            ->setResponsibleUserId($lead->getResponsibleUserId())
            ->setIsCompleted(false)
            ->setText(rand(0, 100))
            ->setEntityId($lead->getId())
            ->setEntityType($lead->getType());

        $completeTill = $this->calculateDueDate();

        if ($completeTill === null) {
            return null;
        }

        $duration = abs($completeTill->diffInSeconds(Carbon::now()));

        $task
            ->setCompleteTill($completeTill->getTimestamp())
            ->setDuration($duration);

        try {
            return $this->client->tasks()->addOne($task);
        } catch (Throwable) {
            return null;
        }
    }

    /**
     * Создает Carbon с минимальной длительностью 4 дня
     *
     * @return Carbon|null
     */
    private function calculateDueDate(): ?Carbon
    {
        $now = Carbon::now();
        $workStart = $now->copy()->setTime(9, 0);
        $workEnd = $now->copy()->setTime(18, 0);

        if ($now->gt($workEnd)) {
            $now->addDay()->setTime(9, 0);
        } else if ($now->lt($workStart)) {
            $now->setTime(9, 0);
        }

        if ($now->isWeekend()) {
            $now->next(CarbonInterface::MONDAY);
        }

        $completeTill = $now;
        for ($i = 0; $i < 4; $i++) {
            $completeTill->addDay();
            while ($completeTill->isWeekend()) {
                $completeTill->addDay();
            }
        }

        $completeTill->setTime(18, 0);

        return $completeTill;
    }
}
