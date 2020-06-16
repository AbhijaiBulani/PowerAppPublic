package com.abhijai.powerfulandroidapp

import android.util.Log
import com.abhijai.powerfulandroidapp.session.SessionManager
import com.abhijai.powerfulandroidapp.ui.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),DataStateChangeListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?)
    {
        dataState?.let {
            GlobalScope.launch(Main) {

                //Log.d("BaseActivity", "onDataStateChange -> DataState :  $it")
                Log.d("BaseActivity", "onDataStateChange -> DataState Loading :  ${it.loading.isLoading}")
                Log.d("BaseActivity", "onDataStateChange -> DataState Data : ${it.data}")
                Log.d("BaseActivity", "onDataStateChange -> DataState Error : ${it.error}")
                displayProgressBar(it.loading.isLoading)

                it.error?.let {errorEvent->
                    handleStateError(errorEvent)
                }

                it.data?.let {
                    it.response?.let {responseEvent->
                        handleStateResponse(responseEvent)
                    }
                }
                if (it.data==null){
                    Log.e("BaseActivity", "onDataStateChange-> Data is ${it.data}")
                }
            }
        }
    }

    private fun handleStateResponse(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when(it.responseType){
                is ResponseType.Toast->{
                    it.message?.let {
                        displayToast(it)
                    }
                }
                is ResponseType.Dialog->{
                    it.message?.let {
                        displaySuccessDialog(message = it)
                    }
                }
                is ResponseType.None->{
                    it.message?.let {message->
                        Log.d("BaseActivity", "handleStateResponse ${message}")
                    }
                }
            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>)
    {
        errorEvent.getContentIfNotHandled()?.let {
            Log.d("BaseActivity", "handleStateError ${it.response.message}")
            when(it.response.responseType){
                is ResponseType.Toast->{
                    it.response.message?.let {
                        displayToast(it)
                    }
                }
                is ResponseType.Dialog->{
                    it.response.message?.let {
                        displayErrorDialog(message = it)
                    }
                }
                is ResponseType.None->{
                    it.response.message?.let {errorMessage->
                        Log.e("BaseActivity", "handleStateError ${errorMessage}")
                    }
                }
            }
        }
    }

    //Every activity will have a different progressbar widget in it, that's why we are making it abstract
    abstract fun displayProgressBar(bool:Boolean)
}