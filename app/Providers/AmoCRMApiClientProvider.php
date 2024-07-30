<?php

declare(strict_types=1);

namespace App\Providers;

use AmoCRM\Client\AmoCRMApiClient;
use AmoCRM\Client\LongLivedAccessToken;
use Illuminate\Support\ServiceProvider;

final class AmoCRMApiClientProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->bind(AmoCRMApiClient::class, static function () {
            $client = new AmoCRMApiClient(
                env('AMOCRM_CLIENT_ID'),
                env('AMOCRM_CLIENT_SECRET'),
                env('AMOCRM_CLIENT_REDIRECT_URL')
            );

            $client->setAccountBaseDomain(env('AMOCRM_ACCOUNT_DOMAIN'));

            $token = new LongLivedAccessToken(env('AMOCRM_LONG_TERM_TOKEN'),);
            $client->setAccessToken($token);

            return $client;
        });
    }

    public function boot(): void
    {

    }
}
