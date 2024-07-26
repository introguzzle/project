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

final class LeadService
{
    private AmoCRMApiClient $client;

    public function __construct(AmoCRMApiClient $client)
    {
        $this->client = $client;
    }

    /**
     * Создает сделку и связывает её с контактом
     *
     * @param ContactModel|null $contact
     * @return LeadModel|null
     */
    public function submitLead(
        ?ContactModel $contact = null
    ): ?LeadModel
    {
        $contacts = (new ContactsCollection())
            ->add($contact);

        $lead = (new LeadModel())
            ->setResponsibleUserId($contact->getResponsibleUserId())
            ->setAccountId($contact->getAccountId())
            ->setContacts($contacts);

        try {
            $lead = $this->client->leads()->addOne($lead);

            $this->client->leads()->link(
                $lead,
                (new LinksCollection())
                    ->add($this->createCatalogElement('Товар 1'))
                    ->add($this->createCatalogElement('Товар 2'))
            );

            return $lead;
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }

    /**
     * Возвращает каталог продуктов, если он есть в аккаунте
     * @return CatalogModel|null
     */
    private function getProductCatalog(): ?CatalogModel
    {
        try {
            /**
             * @var CatalogModel $catalog
             */

            foreach ($this->client->catalogs()->get() as $catalog) {
                if ($catalog->getCatalogType() === 'products') {
                    return $catalog;
                }
            }

        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }

        return null;
    }

    /**
     * Создает случайный элемент в списке
     * @param string $name
     * @return CatalogElementModel|null
     */
    private function createCatalogElement(string $name): ?CatalogElementModel
    {
        $catalog = $this->getProductCatalog();

        $catalogElement = (new CatalogElementModel())
            ->setName($name)
            ->setCatalogId($catalog->getId())
            ->setQuantity(1);

        try {
            return $this->client->catalogElements($catalog->getId())
                ->addOne($catalogElement)
                ->setAccountId($catalog->getAccountId());
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }
}
