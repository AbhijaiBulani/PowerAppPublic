package com.abhijai.powerfulandroidapp.ui.auth.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthStateEvent
import com.abhijai.powerfulandroidapp.ui.auth.state.LoginFields
import com.abhijai.powerfulandroidapp.util.ApiEmptyResponse
import com.abhijai.powerfulandroidapp.util.ApiErrorResponse
import com.abhijai.powerfulandroidapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_login.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {

    private val TAG ="OpenApiResponse";
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LoginFragment", "onViewCreated (line 28): ${authViewModel.hashCode()}")
        subscribeToObserver()
        login_button.setOnClickListener {
            login()
        }
    }

    private fun subscribeToObserver(){
        authViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let {
                /*it.login_email?.let {emailStringValue ->  input_email.setText(emailStringValue)}?:input_email.setText("No data")*/
                it.login_email?.let {emailStringValue ->  input_email.setText(emailStringValue)}
                it.login_password?.let {passwordStringValue ->  input_password.setText(passwordStringValue)}
            }
        })
    }

    private fun login(){
        authViewModel.setStateEvent(
            AuthStateEvent.LoginAttemptEvent(input_email.text.toString(), input_password.text.toString())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LoginFragment", "onDestroyView (line 63): ")
        authViewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }


}
