<?php

declare(strict_types = 1);

namespace App\Other;

enum Gender : string
{
    case MALE = 'Мужской';
    case FEMALE = 'Женский';
}
