package com.abhijai.powerfulandroidapp.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import com.abhijai.powerfulandroidapp.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
    @Inject
    constructor(val authTokenDao:AuthTokenDao, val application: Application)
{
    private val _cachedToken = MutableLiveData<AuthTokenModel>()

    val cachedToken : LiveData<AuthTokenModel>
    get() = _cachedToken

    fun login(newValue : AuthTokenModel?){
        setValue(newValue)
    }

    private fun setValue(newValue: AuthTokenModel?) {
        GlobalScope.launch(Main) {
            /*if (newValue!=null)// null pe condition htaani hogi taaki jb logout ho toh token ki value null kr ske
            {
                if (_cachedToken.value!=newValue){
                    _cachedToken.value =newValue
                }
            }*/
            if (_cachedToken.value!=newValue){
                _cachedToken.value =newValue
            }
        }
    }

    fun logout(){
        var errorMessage : String?=null
        GlobalScope.launch(IO) {
            try{
                _cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                } ?: throw CancellationException("Token Error. Logging out user.")
            }
            catch (ex:CancellationException){
                Log.d("SessionManager", "logout : "+ex.message)
                errorMessage = ex.message
            }
            catch (ex:Exception){
                Log.d("SessionManager", "logout : "+ex.message)
                errorMessage = errorMessage+"\n"+ex.message
            }
            finally {
                errorMessage?.let {
                    Log.d("SessionManager", "Finally (line 58): $it")
                }
                setValue(null)
            }
        }
    }

    fun isConnectedToTheInternet(): Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        }catch (e: Exception){
            Log.e("SessionManager", "isConnectedToTheInternet (line 77): ${e.message}")
        }
        return false
    }
}