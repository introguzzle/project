@php use Illuminate\Support\ViewErrorBag; @endphp
@php
    /**
     * @var ViewErrorBag $errors
     */
@endphp

    <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Форма</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        body {
            padding: 20px;
        }

        .container {
            max-width: 600px;
        }
    </style>
</head>
<body>
<div class="container justify-content-center align-items-center">
    <h2 class="mb-4">Отправить информацию</h2>
    <div id="alert-container"></div>
    <form id="data-form" class="w-100">
        @csrf
        <div class="form-group">
            <label for="first_name">Имя</label>
            <input type="text" class="form-control" id="first_name" name="first_name" required>
            <div class="invalid-feedback" id="first_name-error"></div>
        </div>
        <div class="form-group">
            <label for="last_name">Фамилия</label>
            <input type="text" class="form-control" id="last_name" name="last_name" required>
            <div class="invalid-feedback" id="last_name-error"></div>
        </div>
        <div class="form-group">
            <label for="age">Возраст</label>
            <input type="number" class="form-control" id="age" name="age" required>
            <div class="invalid-feedback" id="age-error"></div>
        </div>
        <div class="form-group">
            <label for="gender">Пол</label>
            <select class="form-control" id="gender" name="gender" required>
                <option value="male">Мужской</option>
                <option value="female">Женский</option>
            </select>
            <div class="invalid-feedback" id="gender-error"></div>
        </div>
        <div class="form-group">
            <label for="phone">Телефон</label>
            <input type="text" class="form-control" id="phone" name="phone" required>
            <div class="invalid-feedback" id="phone-error"></div>
        </div>
        <div class="form-group">
            <label for="email">Почта</label>
            <input type="email" class="form-control" id="email" name="email" required>
            <div class="invalid-feedback" id="email-error"></div>
        </div>
        <button type="submit" class="w-100 btn btn-primary">Отправить</button>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script>
    $(document).ready(function () {
        $('#data-form').on('submit', function (e) {
            const token = $('input[name="_token"]').val();

            e.preventDefault();
            const formData = {
                first_name: $('#first_name').val(),
                last_name: $('#last_name').val(),
                age: $('#age').val(),
                gender: $('#gender').val(),
                phone: $('#phone').val(),
                email: $('#email').val(),
                _token: token
            };

            $.ajax({
                url: '{{ route('endpoint.submit') }}',
                type: 'POST',
                contentType: 'application/json',
                headers: {
                    'X-CSRF-TOKEN': token,
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(formData),
                success: function (response) {
                    const debugContainer = $('<pre></pre>').text(JSON.stringify(response, null, 2));

                    $('#alert-container')
                        .html('<div class="alert alert-success">' + response.success + '</div>')
                        .append(debugContainer);

                    clearForm();
                },
                error: function (xhr) {
                    if (xhr.status === 500) {
                        $('#alert-container').html('<div class="alert alert-danger">Произошла ошибка на сервере. Попробуйте позже.</div>');
                    } else {
                        displayErrors(xhr.responseJSON.errors);
                    }
                }
            });
        });

        function clearForm() {
            $('#data-form')[0].reset();
            $('.invalid-feedback').text('');
            $('.form-control').removeClass('is-invalid');
        }

        function displayErrors(errors) {
            for (const field in errors) {
                if (errors.hasOwnProperty(field)) {
                    $('#' + field + '-error').text(errors[field][0]);
                    $('#' + field).addClass('is-invalid');
                }
            }
        }
    });
</script>
</body>
</html>
