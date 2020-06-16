package com.abhijai.powerfulandroidapp.di

import com.abhijai.powerfulandroidapp.di.auth.AuthFragmentBuildersModule
import com.abhijai.powerfulandroidapp.di.auth.AuthModule
import com.abhijai.powerfulandroidapp.di.auth.AuthScope
import com.abhijai.powerfulandroidapp.di.auth.AuthViewModelModule
import com.abhijai.powerfulandroidapp.di.main.MainFragmentBuildersModule
import com.abhijai.powerfulandroidapp.di.main.MainModule
import com.abhijai.powerfulandroidapp.di.main.MainScope
import com.abhijai.powerfulandroidapp.di.main.MainViewModelModule
import com.abhijai.powerfulandroidapp.ui.auth.AuthActivity
import com.abhijai.powerfulandroidapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}