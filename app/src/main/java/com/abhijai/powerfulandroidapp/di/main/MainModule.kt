package com.abhijai.powerfulandroidapp.di.main

import com.abhijai.powerfulandroidapp.api.main.OpenApiMainService
import com.abhijai.powerfulandroidapp.persistence.AccountPropertiesDao
import com.abhijai.powerfulandroidapp.repositories.main.AccountRepository
import com.abhijai.powerfulandroidapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder):OpenApiMainService{
        return retrofitBuilder.build().create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ):AccountRepository
    {
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }

}