<?php

declare(strict_types=1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Models\LeadModel;
use AmoCRM\Models\TaskModel;
use Carbon\Carbon;
use DateTimeZone;
use Illuminate\Support\Facades\Log;
use Throwable;

final readonly class TaskService
{
    public function __construct(
        private AmoCRMApiClient $client
    ) {}

    /**
     * Создает задачу и привязывает её к сделку
     *
     * @param LeadModel $lead
     * @return TaskModel|null
     */
    public function submitTask(LeadModel $lead): ?TaskModel
    {
        $task = (new TaskModel())
            ->setResponsibleUserId($lead->getResponsibleUserId())
            ->setIsCompleted(false)
            ->setText((string)$lead->getId())
            ->setEntityId($lead->getId())
            ->setEntityType($lead->getType());

        $completeTill = $this->calculateDueDate();

        if ($completeTill === null) {
            return null;
        }

        $task
            ->setCompleteTill($completeTill->getTimestamp())
            ->setDuration(60 * 60 * 9); // 9:00 - 18:00

        try {
            return $this->client->tasks()->addOne($task);
        } catch (Throwable $t) {
            Log::error($t);
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
        $completeTill = self::now()->addDays(4);

        while ($completeTill->isWeekend()) {
            $completeTill->addDay();
        }

        $completeTill->setTime(9, 0);

        return $completeTill;
    }

    private static function now(): Carbon
    {
        return Carbon::now(new DateTimeZone('Europe/Moscow'));
    }
}
