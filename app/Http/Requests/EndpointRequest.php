<?php
declare(strict_types=1);

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;

/**
 * @property string $first_name
 * @property string $last_name
 * @property string $email
 * @property string $phone
 * @property string $age
 * @property string $gender
 */

final class EndpointRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    /**
     * @return array<string, ValidationRule|array|string>
     */
    public function rules(): array
    {
        return [
            'first_name' => 'required|string|max:255',
            'last_name'  => 'required|string|max:255',
            'email'      => 'required|email',
            'phone'      => 'required|string|max:20',
            'age'        => 'required|integer|min:0|max:120',
            'gender'     => 'required|in:male,female',
        ];
    }
}
