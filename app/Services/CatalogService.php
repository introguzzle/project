<?php

declare(strict_types=1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Exceptions\InvalidArgumentException;
use AmoCRM\Helpers\EntityTypesInterface;
use AmoCRM\Models\CatalogElementModel;
use AmoCRM\Models\CatalogModel;
use Illuminate\Support\Facades\Log;
use Throwable;

final readonly class CatalogService
{
    public function __construct(
        private AmoCRMApiClient $client
    ) {}

    /**
     * Возвращает каталог продуктов, если он есть в аккаунте
     * @return CatalogModel|null
     */
    public function getProductCatalog(): ?CatalogModel
    {
        try {
            /**
             * @var CatalogModel $catalog
             */
            foreach ($this->client->catalogs()->get() as $catalog) {
                if ($catalog->getCatalogType() === EntityTypesInterface::PRODUCTS) {
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
     * @throws InvalidArgumentException
     */
    public function createCatalogElement(string $name): ?CatalogElementModel
    {
        $catalog = $this->getProductCatalog();

        if ($catalog === null) {
            return null;
        }

        $catalogElement = (new CatalogElementModel())
            ->setName($name)
            ->setCatalogId($catalog->getId())
            ->setQuantity(1);

        try {
            return $this->client->catalogElements($catalog->getId())
                ->addOne($catalogElement);
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }
    }
}
