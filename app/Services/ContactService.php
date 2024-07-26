<?php
declare(strict_types = 1);

namespace App\Services;

use AmoCRM\Client\AmoCRMApiClient;

use AmoCRM\Collections\ContactsCollection;
use AmoCRM\Collections\CustomFields\CustomFieldsCollection;
use AmoCRM\Collections\CustomFieldsValuesCollection;

use AmoCRM\Exceptions\AmoCRMApiException;
use AmoCRM\Exceptions\AmoCRMMissedTokenException;
use AmoCRM\Exceptions\AmoCRMoAuthApiException;
use AmoCRM\Exceptions\InvalidArgumentException;

use AmoCRM\Models\ContactModel;
use AmoCRM\Models\Customers\CustomerModel;
use AmoCRM\Models\CustomFields\CustomFieldModel;
use AmoCRM\Models\CustomFields\TextCustomFieldModel;
use AmoCRM\Models\CustomFieldsValues\BaseCustomFieldValuesModel;
use AmoCRM\Models\CustomFieldsValues\ValueCollections\BaseCustomFieldValueCollection;
use AmoCRM\Models\CustomFieldsValues\ValueModels\BaseCustomFieldValueModel;
use AmoCRM\Models\UserModel;

use App\Other\ContactResult;
use Illuminate\Support\Facades\Log;
use Throwable;

final class ContactService
{
    private const ENTITY_TYPE = 'contacts';
    private const SUCCESS_CODE = 142;
    private CustomFieldsCollection $cachedCustomFields;
    private AmoCRMApiClient $client;

    public function __construct(AmoCRMApiClient $client)
    {
        $this->client = $client;
    }

    /**
     * Создает контакт на основе данных из массива.
     *
     * @param array $data
     * @return ContactResult
     */
    public function submitContact(array $data): ContactResult
    {
        try {
            /**
             * Проверяем, есть ли уже контакт.
             * Если его нет, переходим к созданию нового
             */

            $existingContact = $this->findContactByPhone($data['phone']);
            if ($existingContact !== null) {
                $allAreSuccess = true;
                $linkedLeads = $existingContact->getLeads();

                if ($linkedLeads !== null && !$linkedLeads->isEmpty()) {
                    foreach ($linkedLeads as $lead) {
                        $fetchedLead = $this->client->leads()->getOne($lead->getId());
                        $allAreSuccess &= ((int) $fetchedLead->getStatusId() === self::SUCCESS_CODE);
                    }
                }

                if ($allAreSuccess) {
                    $this->createCustomer($existingContact);
                }

                return new ContactResult($existingContact, $linkedLeads->isEmpty());
            }

            /**
             * Создаем новый контакт
             */

            $customFieldsValuesCollection = (new CustomFieldsValuesCollection())
                ->add($this->createCustomFieldValues('Почта', $data['email']))
                ->add($this->createCustomFieldValues('Телефон', $data['phone']))
                ->add($this->createCustomFieldValues('Пол', $data['gender'] === 'male'
                    ? 'Мужской'
                    : 'Женский')
                );

            $contact = (new ContactModel())
                ->setFirstName($data['first_name'])
                ->setLastName($data['last_name'])
                ->setCustomFieldsValues($customFieldsValuesCollection)
                ->setResponsibleUserId($this->getRandomUser()->getId())
                ->setAccountId($this->client->account()->getCurrent()->getId());

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
     * @param string $fieldName
     * @return array
     */
    private function getFieldValues(
        ContactModel $contact,
        string $fieldName
    ): array
    {
        $values = [];

        /**
         * @var BaseCustomFieldValuesModel $customFieldValue
         * @var BaseCustomFieldValueModel $value
         */
        foreach ($contact->getCustomFieldsValues() as $customFieldValue) {
            foreach ($customFieldValue->getValues() as $value) {
                if ((string) $customFieldValue->getFieldName() === $fieldName) {
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
            $contactsCollection = $this->client->contacts()->get(null, ['leads', 'statuses']);

            foreach ($contactsCollection as $contact) {
                $contactPhones = $this->getFieldValues($contact, 'Телефон');
                if (in_array($phone, $contactPhones)) {
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

        try {
            return $usersCollection[rand(0, $usersCollection->count() - 1)];
        } catch (Throwable) {
            return $usersCollection[0];
        }
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
            $this->client->customFields(self::ENTITY_TYPE)->get();
    }

    /**
     * Ищет пользовательское поле по имени.
     *
     * @param string $name
     * @throws InvalidArgumentException
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @throws AmoCRMoAuthApiException
     * @return CustomFieldModel|null
     */
    private function findCustomField(string $name): ?CustomFieldModel
    {
        $customFieldsCollection = $this->getCustomFields();

        /**
         * @var CustomFieldModel $customField
         */
        foreach ($customFieldsCollection as $customField) {
            if ((string) $customField->getName() === $name) {
                return $customField;
            }
        }

        return null;
    }

    /**
     * Создает значения пользовательских полей.
     *
     * @param string $name
     * @param string $value
     * @throws InvalidArgumentException
     * @throws AmoCRMApiException
     * @throws AmoCRMMissedTokenException
     * @throws AmoCRMoAuthApiException
     * @return BaseCustomFieldValuesModel
     */
    private function createCustomFieldValues(
        string $name,
        string $value
    ): BaseCustomFieldValuesModel
    {
        $customField = $this->findCustomField($name);

        if ($customField === null) {
            $model = (new TextCustomFieldModel())
                ->setName($name)
                ->setEntityType(self::ENTITY_TYPE);

            $customField = $this->client
                ->customFields(self::ENTITY_TYPE)
                ->addOne($model);
        }

        $model = new BaseCustomFieldValueModel();
        $model->setValue($value);

        $valuesCollection = new BaseCustomFieldValueCollection();
        $valuesCollection->add($model);

        return (new BaseCustomFieldValuesModel())
            ->setFieldId($customField->getId())
            ->setFieldCode($customField->getCode())
            ->setFieldName($customField->getName())
            ->setValues($valuesCollection);
    }


    /**
     * Создает покупателя на основе данных контакта.
     *
     * @param ContactModel $contact
     * @return CustomerModel|null
     */
    private function createCustomer(ContactModel $contact): ?CustomerModel
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
