package com.abhijai.powerfulandroidapp.api.auth

import androidx.lifecycle.LiveData
import com.abhijai.powerfulandroidapp.api.auth.network_responses.LoginResponse
import com.abhijai.powerfulandroidapp.api.auth.network_responses.RegistrationResponse
import com.abhijai.powerfulandroidapp.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @Field is used to send @FormUrlEncoded request in Retrofit which hides your parameter and not attach with url to provide security.
 * Used for POST request. @Query parameter appended to the URL.
 * If you are using @Query request than all your parameter is append to your request and visible to users.
 */

interface OpenApiAuthService{

    //https://open-api.xyz/api/account/login
    @POST("account/login")
    @FormUrlEncoded
    fun loginToServer(
        @Field("username") email:String,
        @Field("password")password:String
    ):LiveData<GenericApiResponse<LoginResponse>>

    //https://open-api.xyz/api/account/register
    @POST("account/register")
    @FormUrlEncoded
    fun registerToServer(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>
}