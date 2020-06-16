package com.abhijai.powerfulandroidapp.ui.auth

import androidx.lifecycle.LiveData
import com.abhijai.powerfulandroidapp.api.auth.network_responses.LoginResponse
import com.abhijai.powerfulandroidapp.api.auth.network_responses.RegistrationResponse
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.repositories.auth.AuthRepository
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthStateEvent
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthViewState
import com.abhijai.powerfulandroidapp.ui.auth.state.LoginFields
import com.abhijai.powerfulandroidapp.ui.auth.state.RegistrationFields
import com.abhijai.powerfulandroidapp.util.AbsentLiveData
import com.abhijai.powerfulandroidapp.util.BaseViewModel
import com.abhijai.powerfulandroidapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
    @Inject
    constructor(val authRepository: AuthRepository) : BaseViewModel<AuthStateEvent,AuthViewState>()
{
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            is AuthStateEvent.LoginAttemptEvent->{
                return authRepository.attemptLogin(stateEvent.email,stateEvent.password)
            }
            is AuthStateEvent.RegisterAttemptEvent->{
                return authRepository.attemptRegistration(stateEvent.email,
                stateEvent.userName,
                stateEvent.password,
                stateEvent.confirmPassword)
            }
            is AuthStateEvent.CheckPreviousAuthEvent->{
                return authRepository.checkPreviousAuthUser()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    // Now we need to create setters for each and every parameters in AuthViewState constructor

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields==registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setPreviousAuthState(authTokenModel: AuthTokenModel){
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authTokenModel){
            return
        }
        update.authToken = authTokenModel
        _viewState.value=update
    }

    fun cancelRepositoryJob(){
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelRepositoryJob()
    }

}