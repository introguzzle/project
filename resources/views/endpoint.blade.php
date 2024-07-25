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
    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

    @if (session('error'))
        <div class="alert alert-danger">
            {{ session('error') }}
        </div>
    @endif

    <form class="w-100" action="{{ route('endpoint.submit') }}" method="POST">
        @csrf
        <div class="form-group">
            <label for="first_name">Имя</label>
            <input type="text" class="form-control @error('first_name') is-invalid @enderror" id="first_name"
                   name="first_name" required>
            @error('first_name')
                <div class="invalid-feedback">{{ $errors->first('first_name') }}</div>
            @enderror
        </div>
        <div class="form-group">
            <label for="last_name">Фамилия</label>
            <input type="text" class="form-control @error('last_name') is-invalid @enderror" id="last_name"
                   name="last_name" required>
            @error('last_name')
                <div class="invalid-feedback">{{ $errors->first('last_name') }}</div>
            @enderror
        </div>
        <div class="form-group">
            <label for="age">Возраст</label>
            <input type="number" class="form-control @error('age') is-invalid @enderror" id="age" name="age" required>
            @error('age')
                <div class="invalid-feedback">{{ $errors->first('age') }}</div>
            @enderror
        </div>
        <div class="form-group">
            <label for="gender">Пол</label>
            <select class="form-control @error('gender') is-invalid @enderror" id="gender" name="gender" required>
                <option value="male">Мужской</option>
                <option value="female">Женский</option>
            </select>
            @error('gender')
                <div class="invalid-feedback">{{ $errors->first('gender') }}</div>
            @enderror
        </div>
        <div class="form-group">
            <label for="phone">Телефон</label>
            <input type="text" class="form-control @error('phone') is-invalid @enderror" id="phone" name="phone"
                   required>
            @error('phone')
                <div class="invalid-feedback">{{ $errors->first('phone') }}</div>
            @enderror
        </div>
        <div class="form-group">
            <label for="email">Почта</label>
            <input type="email" class="form-control @error('email') is-invalid @enderror" id="email" name="email"
                   required>
            @error('email')
                <div class="invalid-feedback">{{ $errors->first('email') }}</div>
            @enderror
        </div>
        <button type="submit" class="w-100 btn btn-primary">Отправить</button>
    </form>
</div>
</body>
</html>
