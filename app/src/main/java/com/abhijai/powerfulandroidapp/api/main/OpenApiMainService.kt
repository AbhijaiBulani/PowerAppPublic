package com.abhijai.powerfulandroidapp.api.main

import androidx.lifecycle.LiveData
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization:String
    ):LiveData<GenericApiResponse<AccountPropertiesModel>>
}