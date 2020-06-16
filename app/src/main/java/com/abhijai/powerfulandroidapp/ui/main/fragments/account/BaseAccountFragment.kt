package com.abhijai.powerfulandroidapp.ui.main.fragments.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.di.ViewModelProviderFactory
import com.abhijai.powerfulandroidapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseAccountFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener
    @Inject
    lateinit var providerFactory : ViewModelProviderFactory

    lateinit var accountViewModel : AccountViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)
        accountViewModel = activity?.let {
            ViewModelProvider(this,providerFactory).get(AccountViewModel::class.java)
        }?:throw Exception("Invalid Activity")
    }

    fun setUpActionBarWithNavController(fragmentId : Int, activity : AppCompatActivity)
    {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(activity,findNavController(),appBarConfiguration)
    }
}