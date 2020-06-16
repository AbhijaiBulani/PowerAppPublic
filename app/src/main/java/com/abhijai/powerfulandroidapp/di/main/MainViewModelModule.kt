package com.abhijai.powerfulandroidapp.di.main

import androidx.lifecycle.ViewModel
import com.abhijai.powerfulandroidapp.di.ViewModelKey
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel):ViewModel

}