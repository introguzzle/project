<?php

declare(strict_types=1);

namespace App\Other;

use AmoCRM\Models\ContactModel;
use JsonSerializable;

final class ContactResult implements JsonSerializable
{
    public function __construct(
        private ?ContactModel $contactModel = null,
        private bool $shouldCreateLead = false
    ) {}

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
