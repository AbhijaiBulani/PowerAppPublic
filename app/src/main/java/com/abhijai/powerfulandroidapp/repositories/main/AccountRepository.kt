package com.abhijai.powerfulandroidapp.repositories.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap
import com.abhijai.powerfulandroidapp.api.main.OpenApiMainService
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.persistence.AccountPropertiesDao
import com.abhijai.powerfulandroidapp.repositories.NetworkBoundResource
import com.abhijai.powerfulandroidapp.session.SessionManager
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountViewState
import com.abhijai.powerfulandroidapp.util.ApiSuccessResponse
import com.abhijai.powerfulandroidapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
    @Inject
    constructor(
        val openApiMainService: OpenApiMainService,
        val accountPropertiesDao: AccountPropertiesDao,
        val sessionManager: SessionManager
    )
{

    private var repositoryJobs : Job?=null

    private var accountViewStateLiveData : MutableLiveData<AccountViewState> = MutableLiveData()

    fun getAccountProperties(authToken : AuthTokenModel):LiveData<DataState<AccountViewState>>
    {
        return object : NetworkBoundResource<AccountPropertiesModel,AccountPropertiesModel,AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true
        ){
            override suspend fun createCacheRequestAndReturn()
            {

                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }

                /*withContext(Main){
                    result.addSource(loadFromCache(), Observer {
                        onCompleteJob(
                            DataState.data(
                                data = it,
                                response = null
                            )
                        )
                    })
                }*/
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountPropertiesModel>) {
                updateLocalDB(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountPropertiesModel>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token}")
            }

            override fun setJob(job: Job) {
                repositoryJobs?.cancel()
                repositoryJobs = job
            }

            override fun loadFromCache(): LiveData<AccountViewState>
            {

                // Error -> (AppCrashed)This source was already added with the different observer
                /* val liveData = accountPropertiesDao.searchAccountPropertiesUsingPk(authToken.account_pk!!)
                liveData.switchMap {
                    handleLiveData(it)
                }
                return accountViewStateLiveData*/

                // No Error in this style
                return accountPropertiesDao.searchAccountPropertiesUsingPk(authToken.account_pk!!)
                .switchMap {
                     handleLiveData(it)
                }
                //return accountViewStateLiveData

                // Error -> it get observed again and again whenever configuration changes
               /* return accountPropertiesDao.searchAccountPropertiesUsingPk(authToken.account_pk!!)
                    .switchMap {
                        object: LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }*/

            }

            override suspend fun updateLocalDB(cacheObject: AccountPropertiesModel?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        pk=it.pk,
                        email = it.email,
                        username = it.username
                    )
                }
            }

        }.asLiveData()
    }

    private fun handleLiveData(model : AccountPropertiesModel):LiveData<AccountViewState>{
        accountViewStateLiveData.value = AccountViewState(model)
        return accountViewStateLiveData
    }

    fun cancelActiveJobs(){
        Log.d("AccountRepository", "cancelActiveJobs ->  canceling active jobs")
    }

}