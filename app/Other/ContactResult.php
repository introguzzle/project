<?php
declare(strict_types=1);

namespace App\Other;

use AmoCRM\Models\ContactModel;
use JsonSerializable;

final class ContactResult implements JsonSerializable
{
    private ?ContactModel $contactModel;
    private bool $shouldCreateLead;

    public function __construct(
        ContactModel $contactModel = null,
        bool $shouldCreateLead = false
    )
    {
        $this->contactModel = $contactModel;
        $this->shouldCreateLead = $shouldCreateLead;
    }

    public function getContactModel(): ?ContactModel
    {
        return $this->contactModel;
    }

    public function setContactModel(?ContactModel $contactModel): ContactResult
    {
        $this->contactModel = $contactModel;
        return $this;
    }

    public function shouldCreateLead(): bool
    {
        return $this->shouldCreateLead;
    }

    public function setShouldCreateLead(bool $shouldCreateLead): ContactResult
    {
        $this->shouldCreateLead = $shouldCreateLead;
        return $this;
    }

    public function jsonSerialize(): mixed
    {
        return [
            'contact_model'      => $this->contactModel === null ? 'null' : 'present',
            'should_create_lead' => $this->shouldCreateLead,
        ];
    }
}
