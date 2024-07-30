<?php

declare(strict_types=1);

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Http\Exceptions\HttpResponseException;
use Symfony\Component\HttpFoundation\Response;

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

    public function messages(): array
    {
        return [
            'first_name.required' => 'Имя обязательно для заполнения.',
            'first_name.string'   => 'Имя должно быть строкой.',
            'first_name.max'      => 'Имя не должно превышать 255 символов.',
            'last_name.required'  => 'Фамилия обязательна для заполнения.',
            'last_name.string'    => 'Фамилия должна быть строкой.',
            'last_name.max'       => 'Фамилия не должна превышать 255 символов.',
            'email.required'      => 'Электронная почта обязательна для заполнения.',
            'email.email'         => 'Введите корректный адрес электронной почты.',
            'phone.required'      => 'Телефон обязателен для заполнения.',
            'phone.string'        => 'Телефон должен быть строкой.',
            'phone.max'           => 'Телефон не должен превышать 20 символов.',
            'age.required'        => 'Возраст обязателен для заполнения.',
            'age.integer'         => 'Возраст должен быть числом.',
            'age.min'             => 'Возраст не может быть меньше 0.',
            'age.max'             => 'Возраст не может быть больше 120.',
            'gender.required'     => 'Пол обязателен для заполнения.',
            'gender.in'           => 'Пол должен быть либо мужским, либо женским.',
        ];
    }

    /**
     * @return array<string, string>
     */
    public function attributes(): array
    {
        return [
            'first_name' => 'имя',
            'last_name'  => 'фамилия',
            'email'      => 'электронная почта',
            'phone'      => 'телефон',
            'age'        => 'возраст',
            'gender'     => 'пол',
        ];
    }

    public function failedValidation(Validator $validator): void
    {
        $errors = $validator->errors();
        $jsonResponse = response()->json(['errors' => $errors], Response::HTTP_BAD_REQUEST);

        throw new HttpResponseException($jsonResponse);
    }
}
