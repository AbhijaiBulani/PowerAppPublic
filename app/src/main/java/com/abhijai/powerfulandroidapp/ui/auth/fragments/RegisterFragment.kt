package com.abhijai.powerfulandroidapp.ui.auth.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.ui.auth.state.AuthStateEvent
import com.abhijai.powerfulandroidapp.ui.auth.state.RegistrationFields
import com.abhijai.powerfulandroidapp.util.ApiEmptyResponse
import com.abhijai.powerfulandroidapp.util.ApiErrorResponse
import com.abhijai.powerfulandroidapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : BaseAuthFragment() {

    private val TAG ="OpenApiResponse";
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RegisterFragment", "onViewCreated (line 28): ${authViewModel.hashCode()}")
        subscribeViewModel()
        register_button.setOnClickListener {
            register()
        }
    }

    private fun subscribeViewModel(){
        authViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let {
                it.registration_email?.let { emailValueInString -> input_email.setText(emailValueInString) }
                it.registration_username?.let { userNameValueInString -> input_username.setText(userNameValueInString) }
                it.registration_password?.let { passwordValueInString -> input_password.setText(passwordValueInString) }
                it.registration_confirm_password?.let { confirmPasswordValueInString -> input_password_confirm.setText(confirmPasswordValueInString) }
            }
        })
    }

    private fun register(){
        authViewModel.setStateEvent(
        AuthStateEvent.RegisterAttemptEvent(
            input_email.text.toString(),
            input_username.text.toString(),
            input_password.text.toString(),
            input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
