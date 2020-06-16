package com.abhijai.powerfulandroidapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.abhijai.powerfulandroidapp.BaseActivity
import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.di.ViewModelProviderFactory
import com.abhijai.powerfulandroidapp.ui.ResponseType
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthStateEvent
import com.abhijai.powerfulandroidapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener{

    @Inject
    lateinit var providerFactory : ViewModelProviderFactory

    lateinit var authViewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AuthActivity", "onCreate (line 30): ")
        setContentView(R.layout.activity_auth)
        authViewModel = ViewModelProvider(this,providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)
        subscribeObserver()
        checkPreviousAuthUser()
    }

    private fun subscribeObserver()
    {
        Log.d("AuthActivityO", "subscribeObserver (line 41): ")
        /**
         * -------------- FLOW --------------------
         * step 1 -> LoginFragment -> setStateEvent
         * step 2 -> now observe dataState(result of step 1), for e.g -> DataState<AuthViewState>
         * step 3 -> now set viewState from dataState
         * step 4 -> now observe viewState.
         */

        authViewModel.dataState.observe(this, Observer {dataStateAuthViewState->
            Log.d("AuthActivityO", "subscribeObserver (line 48): ")
            onDataStateChange(dataStateAuthViewState)
            dataStateAuthViewState.data?.let{dataAuthViewState->
                dataAuthViewState.data?.let {eventAuthViewState->
                    eventAuthViewState.getContentIfNotHandled()?.let {authViewState ->
                        authViewState.authToken?.let {authTokenModel->
                            Log.d("AuthActivityO", "subscribeObserver (line 57): $authTokenModel")
                            authViewModel.setPreviousAuthState(authTokenModel)
                        }
                    }
                }
            }
        })

        authViewModel.viewState.observe(this, Observer {
            it?.let {
                sessionManager.login(it.authToken)
            }
        })

        sessionManager.cachedToken.observe(this, Observer {
            if (it!=null && it.account_pk!=-1 && it.token!=null){
                navigateToMainActivity()
            }
        })
    }

    private fun checkPreviousAuthUser(){
        authViewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun navigateToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        authViewModel.cancelRepositoryJob()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        //ignore, this is only for MainActivity
    }

}
