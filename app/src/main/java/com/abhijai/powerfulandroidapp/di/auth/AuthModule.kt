package com.abhijai.powerfulandroidapp.di.auth

import android.content.SharedPreferences
import com.abhijai.powerfulandroidapp.api.auth.OpenApiAuthService
import com.abhijai.powerfulandroidapp.persistence.AccountPropertiesDao
import com.abhijai.powerfulandroidapp.persistence.AuthTokenDao
import com.abhijai.powerfulandroidapp.repositories.auth.AuthRepository
import com.abhijai.powerfulandroidapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder : Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder.build().create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreference:SharedPreferences,
        sharedPrefsEditor:SharedPreferences.Editor
    ): AuthRepository
    {
        return AuthRepository(authTokenDao, accountPropertiesDao, openApiAuthService, sessionManager,sharedPreference,sharedPrefsEditor)
    }

}