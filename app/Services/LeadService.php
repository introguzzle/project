<?php

declare(strict_types=1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Collections\ContactsCollection;
use AmoCRM\Collections\LinksCollection;
use AmoCRM\Models\CatalogElementModel;
use AmoCRM\Models\CatalogModel;
use AmoCRM\Models\ContactModel;
use AmoCRM\Models\LeadModel;
use Illuminate\Support\Facades\Log;
use Throwable;

final readonly class LeadService
{
    public function __construct(
        private AmoCRMApiClient $client,
        private CatalogService  $catalogService
    ) {}

    /**
     * Создает сделку и связывает её с контактом
     *
     * @param ContactModel|null $contact
     * @return LeadModel|null
     */
    public function submitLead(
        ?ContactModel $contact = null
    ): ?LeadModel {
        if ($contact === null) {
            return null;
        }

        $contacts = (new ContactsCollection())
            ->add($contact);

        $lead = (new LeadModel())
            ->setContacts($contacts);

        try {
            $lead = $this->client->leads()->addOne($lead);

            $this->client->leads()->link(
                $lead,
                (new LinksCollection())
                    ->add($this->catalogService->createCatalogElement('Товар 1'))
                    ->add($this->catalogService->createCatalogElement('Товар 2'))
            );

            return $lead;
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }
}
