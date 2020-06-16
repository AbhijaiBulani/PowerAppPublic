package com.abhijai.powerfulandroidapp.di.auth

import androidx.lifecycle.ViewModel
import com.abhijai.powerfulandroidapp.di.ViewModelKey
import com.abhijai.powerfulandroidapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @AuthScope
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}