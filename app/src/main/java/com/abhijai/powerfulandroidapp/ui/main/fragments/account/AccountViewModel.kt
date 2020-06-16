package com.abhijai.powerfulandroidapp.ui.main.fragments.account

import androidx.lifecycle.LiveData
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.repositories.main.AccountRepository
import com.abhijai.powerfulandroidapp.session.SessionManager
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountStateEvent
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountStateEvent.*
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.state.AccountViewState
import com.abhijai.powerfulandroidapp.util.AbsentLiveData
import com.abhijai.powerfulandroidapp.util.BaseViewModel
import javax.inject.Inject

class AccountViewModel
    @Inject
    constructor(
        val accountRepository: AccountRepository,
        val sessionManager: SessionManager
    ) : BaseViewModel<AccountStateEvent,AccountViewState>()
{

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){
            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let {authTokenModel->
                    accountRepository.getAccountProperties(
                        authTokenModel
                    )
                }?:AbsentLiveData.create()
            }
            is UpdateAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }
            is ChangeAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }
            is None -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties : AccountPropertiesModel){
        val update = getCurrentViewStateOrNew()
        if (update.accountPropertiesModel==accountProperties){
            return
        }
        update.accountPropertiesModel = accountProperties
        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }

}