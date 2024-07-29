<?php

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Collections\ContactsCollection;
use AmoCRM\Models\ContactModel;
use AmoCRM\Models\Customers\CustomerModel;
use Illuminate\Support\Facades\Log;
use Throwable;

final readonly class CustomerService
{
    public function __construct(
        private AmoCRMApiClient $client
    ) {}

    /**
     * Создает покупателя на основе данных контакта.
     *
     * @param ContactModel $contact
     * @return CustomerModel|null
     */
    public function createCustomer(ContactModel $contact): ?CustomerModel
    {
        $customer = (new CustomerModel())
            ->setName($contact->getName())
            ->setAccountId($contact->getAccountId())
            ->setResponsibleUserId($contact->getResponsibleUserId())
            ->setContacts((new ContactsCollection())
                ->add($contact));

        try {
            return $this->client->customers()->addOne($customer);
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }
}
