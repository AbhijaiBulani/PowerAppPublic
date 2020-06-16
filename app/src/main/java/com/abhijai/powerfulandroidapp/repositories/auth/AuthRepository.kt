package com.abhijai.powerfulandroidapp.repositories.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhijai.powerfulandroidapp.api.auth.OpenApiAuthService
import com.abhijai.powerfulandroidapp.api.auth.network_responses.LoginResponse
import com.abhijai.powerfulandroidapp.api.auth.network_responses.RegistrationResponse
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.persistence.AccountPropertiesDao
import com.abhijai.powerfulandroidapp.persistence.AuthTokenDao
import com.abhijai.powerfulandroidapp.repositories.NetworkBoundResource
import com.abhijai.powerfulandroidapp.session.SessionManager
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.Response
import com.abhijai.powerfulandroidapp.ui.ResponseType
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthViewState
import com.abhijai.powerfulandroidapp.ui.auth.state.LoginFields
import com.abhijai.powerfulandroidapp.ui.auth.state.RegistrationFields
import com.abhijai.powerfulandroidapp.util.*
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.abhijai.powerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
    constructor(
            val authTokenDao: AuthTokenDao,
            val accountPropertiesDao: AccountPropertiesDao,
            val openApiAuthService: OpenApiAuthService,
            val sessionManager : SessionManager,
            val sharedPreferences:SharedPreferences,
            val sharedPrefsEditor: SharedPreferences.Editor
)
{
    private var repositoryJob : Job?=null
    private val TAG ="appRepoWa"
    fun attemptLogin(email:String, password:String) : LiveData<DataState<AuthViewState>>
    {
        val loginFieldErrors = LoginFields(email,password).isValidForLogin()
        if (!loginFieldErrors.equals(LoginFields.LoginError.none()))
        {
            return returnErrorResponse(loginFieldErrors,ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse,Any,AuthViewState>(sessionManager.isConnectedToTheInternet(),true,false)
        {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>)
            {
                Log.d("AuthRepositoryO", "handleApiSuccessResponse  ${response}")
                printlnC("handleApiSuccessResponse  ${response}")
                // Handling Invalid loginCredentials(e.g -> from server we are getting invalidCredentials in errorMessage key)
                if (response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage,true,false)
                }

                //don't care about result. Just insert AccountProperties row if it does not exist b'coz of the foreign key relationship with AuthToken Table.
                accountPropertiesDao.insertOrIgnore(AccountPropertiesModel(response.body.pk, response.body.email,""))

                //will result -1 in case of failure
                val result = authTokenDao.insert(AuthTokenModel(response.body.pk,response.body.token))

                if (result<0){
                    return onCompleteJob(DataState.error(Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())))
                }

                saveAuthenticatedUserToPrefs(email)

                onCompleteJob(DataState.data(data = AuthViewState(authToken = AuthTokenModel(response.body.pk,response.body.token))))
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>>
            {
                Log.d("AuthRepository", "createCall -> Email : $email, Password : $password")
                return openApiAuthService.loginToServer(email,password)
            }

            override fun setJob(job: Job)
            {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
                // nothing
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDB(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>>
    {
        val registrationFieldError = RegistrationFields(email,username,password,confirmPassword).isValidForRegistration()
        if (!registrationFieldError.equals(RegistrationFields.RegistrationError.none())){
            return returnErrorResponse(registrationFieldError,ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse,Any,AuthViewState>(sessionManager.isConnectedToTheInternet(),true,false){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d("AuthRepositoryO", "handleApiSuccessResponse ${response}")
                printlnC("handleApiSuccessResponse ${response}")
                if (response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage,true,false)
                }

                //don't care about result. Just insert AccountProperties row if it does not exist b'coz of the foreign key relationship with AuthToken Table.
                accountPropertiesDao.insertOrIgnore(AccountPropertiesModel(response.body.pk, response.body.email,""))

                //will result -1 in case of failure
                val result = authTokenDao.insert(AuthTokenModel(response.body.pk,response.body.token))

                if (result<0){
                    return onCompleteJob(DataState.error(Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())))
                }

                saveAuthenticatedUserToPrefs(email)

                onCompleteJob(DataState.data(data = AuthViewState(authToken = AuthTokenModel(account_pk = response.body.pk,token = response.body.token))))
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.registerToServer(email,username,password,confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob=job
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDB(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser():LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER,null)
        if (previousAuthUserEmail.isNullOrBlank()){
            Log.d("AuthRepositoryO", "checkPreviousAuthUser : No authenticated user found...")
            printlnC("checkPreviousAuthUser : No authenticated user found...")
            return noTokenFound()
        }
        return object : NetworkBoundResource<Void,Any,AuthViewState>(sessionManager.isConnectedToTheInternet(),false,false)
        {
            override suspend fun createCacheRequestAndReturn()
            {
                Log.d("AuthRepository", "createCacheRequestAndReturn (line 157): ")
                val accountPropertiesModel = accountPropertiesDao.searchAccountPropertiesUsingEmail(previousAuthUserEmail)
                accountPropertiesModel.let {
                    Log.d("AuthRepositoryO", "createCacheRequestAndReturn-> searching for token : $accountPropertiesModel")
                    printlnC("createCacheRequestAndReturn-> searching for token : $accountPropertiesModel")
                    accountPropertiesModel?.let {
                        if (it.pk>-1){
                            val authTokenModel = authTokenDao.searchByPk(it.pk)
                            Log.d("AuthRepository", "createCacheRequestAndReturn: AuthToken -> $authTokenModel")
                            authTokenModel?.let {
                                if (it.token==null)
                                {
                                    Log.d("AuthRepository", "TOKEN is NULL")
                                    onCompleteJob(DataState.error(Response("TOKEN IS NULL",ResponseType.None())))
                                }
                                else{
                                    onCompleteJob(DataState.data(data = AuthViewState(authToken = authTokenModel)))
                                }
                                return
                            }
                        }
                    }
                    // if the accountPropertiesModel is null
                    Log.d("AuthRepositoryO", "createCacheRequestAndReturn : AuthToken not found... ")
                    printlnC("createCacheRequestAndReturn : AuthToken not found... ")
                    /**
                     * there is one thing to notice here
                     */
                    onCompleteJob(
                        //DataState.data(data = null,response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None()))
                        DataState.error(Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None()))
                    )
                }
            }

            // not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
                TODO("Not yet implemented")
            }

            // not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
              repositoryJob?.cancel()
                repositoryJob = job
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDB(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    private var noTokenLiveData : MutableLiveData<DataState<AuthViewState>> = MutableLiveData()
    private fun noTokenFound():LiveData<DataState<AuthViewState>>
    {
        Log.d("AuthRepositoryO", "noTokenFound (line 196): ")
        printlnC("noTokenFound (line 203): ")
        /**
         * we have used DataState.Data but not used DataState.error because -> find the answer
         */
        //noTokenLiveData.value = DataState.data(data = null,response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None()))
        noTokenLiveData.value = DataState.error(Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None()))
        return noTokenLiveData
    }

    //-----------------------------

    private var errorLiveData : MutableLiveData<DataState<AuthViewState>> = MutableLiveData()

    private fun returnErrorResponse(errorMessage:String, responseType: ResponseType):LiveData<DataState<AuthViewState>>
    {
        Log.d("AuthRepositoryO", "returnErrorResponse (line 34): $errorMessage")
        printlnC("returnErrorResponse (line 34): $errorMessage")
        errorLiveData.value = DataState.error(Response(message = errorMessage, responseType = responseType))
        return errorLiveData
        /*return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(Response(message = errorMessage, responseType = responseType))
            }
        }*/
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        Log.d("AuthRepository", "saveAuthenticatedUserToPrefs EMAIL $email saved")
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER,email)
        sharedPrefsEditor.apply()
    }

    fun cancelActiveJobs(){
        Log.d("AuthRepositoryO", "cancelActiveJobs...")
        printlnC("cancelActiveJobs...")
        repositoryJob?.cancel()
    }

    private fun printlnC(message : String){
        println("$TAG : $message")
    }

}