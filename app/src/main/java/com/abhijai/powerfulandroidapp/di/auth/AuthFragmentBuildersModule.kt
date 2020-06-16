package com.abhijai.powerfulandroidapp.di.auth

import com.abhijai.powerfulandroidapp.ui.auth.fragments.ForgetPasswordFragment
import com.abhijai.powerfulandroidapp.ui.auth.fragments.LauncherFragment
import com.abhijai.powerfulandroidapp.ui.auth.fragments.LoginFragment
import com.abhijai.powerfulandroidapp.ui.auth.fragments.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgetPasswordFragment

}