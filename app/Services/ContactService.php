<?php

declare(strict_types = 1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Collections\CustomFields\CustomFieldsCollection;
use AmoCRM\Collections\CustomFieldsValuesCollection;
use AmoCRM\Exceptions\AmoCRMApiException;
use AmoCRM\Exceptions\AmoCRMMissedTokenException;
use AmoCRM\Exceptions\AmoCRMoAuthApiException;
use AmoCRM\Exceptions\InvalidArgumentException;
use AmoCRM\Filters\CustomFieldsFilter;
use AmoCRM\Helpers\EntityTypesInterface;
use AmoCRM\Models\ContactModel;
use AmoCRM\Models\CustomFields\CustomFieldModel;
use AmoCRM\Models\CustomFields\TextCustomFieldModel;
use AmoCRM\Models\CustomFieldsValues\BaseCustomFieldValuesModel;
use AmoCRM\Models\CustomFieldsValues\ValueCollections\TextCustomFieldValueCollection;
use AmoCRM\Models\CustomFieldsValues\ValueModels\BaseCustomFieldValueModel;
use AmoCRM\Models\CustomFieldsValues\ValueModels\TextCustomFieldValueModel;
use AmoCRM\Models\UserModel;
use AmoCRM\Models\LeadModel;
use App\Other\ContactResult;
use App\Other\Gender;
use Illuminate\Support\Facades\Log;
use Throwable;

final class ContactService
{
    private const CUSTOM_FIELD_PHONE_ID = 797923;
    private const CUSTOM_FIELD_EMAIL_ID = 872109;

    private const CUSTOM_FIELD_GENDER_ID = 872113;

    private CustomFieldsCollection $cachedCustomFields;

    public function __construct(
        private readonly AmoCRMApiClient $client,
        private readonly CustomerService $customerService,
        private readonly NoteService     $noteService
    ) {}

    /**
     * Создает контакт на основе данных из массива.
     *
     * @param array $data
     * @return ContactResult
     */
    public function handleContact(array $data): ContactResult
    {
        try {
            /*
             * Проверяем, есть ли уже контакт.
             * Если его нет, переходим к созданию нового
             */

            $existingContact = $this->findContactByPhone($data['phone']);
            if ($existingContact !== null) {
                $hasSuccessLead = false;
                $linkedLeads = $existingContact->getLeads();

                if ($linkedLeads !== null && !$linkedLeads->isEmpty()) {
                    /**
                     * @var LeadModel $lead
                     */
                    foreach ($linkedLeads as $lead) {
                        $fetchedLead = $this->client->leads()->getOne($lead->getId());

                        if (($fetchedLead !== null) && ((int)$fetchedLead->getStatusId() === LeadModel::WON_STATUS_ID)) {
                            $hasSuccessLead = true;
                            break;
                        }
                    }
                }

                if ($hasSuccessLead) {
                    $this->customerService->createCustomer($existingContact);
                } else {
                    $this->noteService->createNotUniqueNote($existingContact);
                }

                return new ContactResult($existingContact, $linkedLeads->isEmpty());
            }

            /*
             * Создаем новый контакт
             */

            $customFieldsValuesCollection = (new CustomFieldsValuesCollection())
                ->add($this->createCustomFieldValues(
                    self::CUSTOM_FIELD_EMAIL_ID,
                    'Почта',
                    $data['email'])
                )
                ->add($this->createCustomFieldValues(
                    self::CUSTOM_FIELD_PHONE_ID,
                    'Телефон',
                    $data['phone'])
                )
                ->add($this->createCustomFieldValues(
                    self::CUSTOM_FIELD_GENDER_ID,
                    'Пол',
                    $data['gender'] === 'male'
                        ? Gender::MALE->value
                        : Gender::FEMALE->value
                    )
                );

            $contact = (new ContactModel())
                ->setFirstName($data['first_name'])
                ->setLastName($data['last_name'])
                ->setCustomFieldsValues($customFieldsValuesCollection);

            return new ContactResult($this->client->contacts()->addOne($contact), true);
        } catch (Throwable $t) {
            Log::error($t);
            return new ContactResult(null, false);
        }
    }

    /**
     * Извлекает значения пользовательских полей по имени поля из контакта.
     *
     * @param ContactModel $contact
     * @param int $id
     * @return array
     */
    private function getFieldValues(
        ContactModel $contact,
        int $id,
    ): array {
        $values = [];

        /**
         * @var BaseCustomFieldValuesModel $customFieldValue
         * @var BaseCustomFieldValueModel $value
         */
        foreach ($contact->getCustomFieldsValues() as $customFieldValue) {
            foreach ($customFieldValue->getValues() as $value) {
                if ((int) $customFieldValue->getFieldId() === $id) {
                    $values[] = $value->getValue();
                }
            }
        }

        return $values;
    }

    /**
     * Ищет контакт по номеру телефона.
     *
     * @param string $phone
     * @return ContactModel|null
     */
    private function findContactByPhone(string $phone): ?ContactModel
    {
        try {
            $contactsCollection = $this->client->contacts()->get(
                null,
                [EntityTypesInterface::LEADS]
            );

            /**
             * @var ContactModel $contact
             */

            foreach ($contactsCollection as $contact) {
                $contactPhones = $this->getFieldValues($contact, self::CUSTOM_FIELD_PHONE_ID);
                if (in_array($phone, $contactPhones, true)) {
                    return $contact;
                }
            }
        } catch (Throwable $t) {
            Log::error($t);
            return null;
        }

        return null;
    }

    /**
     * Возвращает случайного пользователя из коллекции пользователей,
     * связанных с текущим аккаунтом
     *
     * @throws AmoCRMoAuthApiException
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @return UserModel|null
     */
    private function getRandomUser(): ?UserModel
    {
        $usersCollection = $this->client->users()->get();

        if ($usersCollection !== null) {
            try {
                return $usersCollection[random_int(0, $usersCollection->count() - 1)];
            } catch (Throwable) {
                return $usersCollection->first();
            }
        }

        return null;
    }

    /**
     * Возвращает коллекцию пользовательских полей,
     * связанных с текущим аккаунтом
     *
     * @throws InvalidArgumentException
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @throws AmoCRMoAuthApiException
     * @return CustomFieldsCollection
     */
    private function getCustomFields(): CustomFieldsCollection
    {
        return $this->cachedCustomFields ??=
            $this->client->customFields(EntityTypesInterface::CONTACTS)->get();
    }

    /**
     * Ищет пользовательское поле по имени.
     *
     * @param int $id
     * @return CustomFieldModel|null
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @throws AmoCRMoAuthApiException
     * @throws InvalidArgumentException
     */
    private function findCustomField(int $id): ?CustomFieldModel
    {
        $customFieldsCollection = $this->getCustomFields();

        /**
         * @var CustomFieldModel $customField
         */
        foreach ($customFieldsCollection as $customField) {
            if ((int) $customField->getId() === $id) {
                return $customField;
            }
        }

        return null;
    }

    /**
     * Создает значения пользовательских полей.
     *
     * @param int $id
     * @param string $name
     * @param string $value
     * @return BaseCustomFieldValuesModel
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @throws AmoCRMoAuthApiException
     * @throws InvalidArgumentException
     */
    private function createCustomFieldValues(
        int $id,
        string $name,
        string $value
    ): BaseCustomFieldValuesModel {
        $customField = $this->findCustomField($id);

        if ($customField === null) {
            $model = (new TextCustomFieldModel())
                ->setName($name)
                ->setEntityType(EntityTypesInterface::CONTACTS);

            $customField = $this->client
                ->customFields(EntityTypesInterface::CONTACTS)
                ->addOne($model);
        }

        $valueModel = new TextCustomFieldValueModel();
        $valueModel->setValue($value);

        $valuesCollection = new TextCustomFieldValueCollection();
        $valuesCollection->add($valueModel);

        return (new BaseCustomFieldValuesModel())
            ->setFieldId($customField->getId())
            ->setFieldCode($customField->getCode())
            ->setFieldName($customField->getName())
            ->setValues($valuesCollection);
    }
}
