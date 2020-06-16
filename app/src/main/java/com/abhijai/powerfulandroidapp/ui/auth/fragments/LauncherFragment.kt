package com.abhijai.powerfulandroidapp.ui.auth.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.abhijai.powerfulandroidapp.R
import kotlinx.android.synthetic.main.fragment_launcher.*

/**
 * A simple [Fragment] subclass.
 */

class LauncherFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login.setOnClickListener {
            navToLoginFragment()
        }
        register.setOnClickListener {
            navToRegisterFragment()
        }
        forgot_password.setOnClickListener {
            navToForgetPasswordFragment()
        }
        Log.d("LauncherFragment", "onViewCreated (line 30): ${authViewModel.hashCode()}")
    }
    private fun navToRegisterFragment() {
       // Log.d(TAG, ": ")
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navToLoginFragment() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navToForgetPasswordFragment(){
        findNavController().navigate(R.id.action_launcherFragment_to_forgetPasswordFragment)
    }


}
