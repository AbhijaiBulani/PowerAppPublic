package com.abhijai.powerfulandroidapp.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.abhijai.powerfulandroidapp.di.ViewModelProviderFactory
import com.abhijai.powerfulandroidapp.ui.auth.AuthViewModel
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseAuthFragment : DaggerFragment() {
    @Inject
    lateinit var providerFactory : ViewModelProviderFactory

    lateinit var authViewModel : AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //authViewModel = ViewModelProvider(this,providerFactory).get(AuthViewModel::class.java)
        authViewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(AuthViewModel::class.java)
        }?: throw Exception("Invalid Activity")

    }
}