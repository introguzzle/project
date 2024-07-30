<?php

declare(strict_types=1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Models\ContactModel;
use AmoCRM\Models\NoteModel;
use AmoCRM\Models\NoteType\CommonNote;
use Illuminate\Support\Facades\Log;
use Throwable;

final readonly class NoteService
{
    public function __construct(
        private AmoCRMApiClient $client
    ) {}

    /**
     * @param ContactModel $contact
     * @return NoteModel|null
     */
    public function createNotUniqueNote(ContactModel $contact): ?NoteModel
    {
        $text = "Была совершена попытка создать дубль контакта с названием {$contact->getFirstName()} {$contact->getLastName()}";
        $note = (new CommonNote())
            ->setText($text)
            ->setEntityId($contact->getId());

        try {
            return $this->client->notes('common')->addOne($note);
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }
}
