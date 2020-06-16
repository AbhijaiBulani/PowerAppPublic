package com.abhijai.powerfulandroidapp

import com.abhijai.powerfulandroidapp.api.auth.network_responses.LoginResponse
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.repositories.NetworkBoundResource
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthViewState
import kotlin.reflect.KClass

class AppConstants {
    companion object {
        const val ACCOUNT_PROPERTIES_TABLE_NAME = "account_properties";
        const val AUTH_TOKEN_TABLE_NAME = "auth_token";

        const val BASE_URL = "https://open-api.xyz/api/"
        const val PASSWORD_RESET_URL: String = "https://open-api.xyz/password_reset/"


          val NETWORK_TIMEOUT = 8000L
        const val CACHE_TIMEOUT = 2000L
         val TESTING_NETWORK_DELAY = 1000L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val rtr ="RAM"


        const val PAGINATION_PAGE_SIZE = 10

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val CROP_IMAGE_INTENT_CODE: Int = 401
    }
}