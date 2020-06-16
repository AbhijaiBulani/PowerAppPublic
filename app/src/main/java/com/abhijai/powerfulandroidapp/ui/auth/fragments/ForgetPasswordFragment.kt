package com.abhijai.powerfulandroidapp.ui.auth.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.abhijai.powerfulandroidapp.AppConstants

import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.DataStateChangeListener
import com.abhijai.powerfulandroidapp.ui.Response
import com.abhijai.powerfulandroidapp.ui.ResponseType
import com.abhijai.powerfulandroidapp.ui.auth.fragments.ForgetPasswordFragment.WebAppInterface.*
import kotlinx.android.synthetic.main.fragment_forget_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class ForgetPasswordFragment : BaseAuthFragment()
{

    private lateinit var webView : WebView

    // why we need DataStateChangeListener in this fragment but not in LoginFragment and RegisterFragment?
    //Ans -> i think we are not using authViewModel in this fragment and that's why we can't set any StateEvent
    //       which will not create any DataState<AuthViewState> and we will not get any AuthViewState to observe in this fragment.
    //       That's why we need DataStateChangeListener explicitly here.
    lateinit var onDataStateChangeListener:DataStateChangeListener

    private val webInteractionCallback: OnWebInteractionCallback = object : OnWebInteractionCallback{
        override fun onSuccess(email: String) {
            Log.d("ForgetPasswordFragment", "onSuccess -> Link is sent to $email ")
            onPasswordLinkSent()
        }

        override fun onError(errorMessage: String) {
            Log.e("ForgetPasswordFragment", "onError -> $errorMessage")
            /**
             * Why we are using <Any> here but not in Repository when we use DataState.error() ?
             *
             */
            onDataStateChangeListener.onDataStateChange(DataState.error<Any>(Response(message = errorMessage,responseType = ResponseType.Dialog())))
        }

        override fun onLoading(isLoading: Boolean) {
            GlobalScope.launch(Main) {
                onDataStateChangeListener.onDataStateChange(DataState.loading(isLoading,null))
            }
        }
    }

    private fun onPasswordLinkSent() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()

            val animation = TranslateAnimation(password_reset_done_container.width.toFloat(),0f,0f,0f)
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? 
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        Log.d("ForgetPasswordFragment", "onViewCreated (line 28): ${authViewModel.hashCode()}")
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
        loadPasswordResetWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPasswordResetWebView(){
        onDataStateChangeListener.onDataStateChange(DataState.loading(true,null))
        webView.webViewClient = object:WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onDataStateChangeListener.onDataStateChange(DataState.loading(false,null))
            }
        }
        webView.loadUrl(AppConstants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        // this is the interface which going to interact with the javaScript on the server
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallback),"AndroidTextListener")
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        try {
            Log.d("ForgetPasswordFragment", "onAttach (line 114): $context")
            onDataStateChangeListener = context as DataStateChangeListener
        }
        catch (ex:ClassCastException){
            Log.e("ForgetPasswordFragment", "onAttach $context must Implement DataStateChangeListener")
        }
    }

    class WebAppInterface constructor(private val callBack:OnWebInteractionCallback)
    {
        /**
         * You will get below error when u using addJavascriptInterface on your webView if you not used the annotation @JavascriptInterface
         * None of the method in the added interface(WebAppInterface) have been annotated with @JavaScriptInterface,
         * this will not be visible in the api 17
         */
        @JavascriptInterface
        fun onSuccess(email:String){
            callBack.onSuccess(email)
        }
        @JavascriptInterface
        fun onError(errorMessage:String){
            callBack.onError(errorMessage)
        }
        @JavascriptInterface
        fun onLoading(isLoading:Boolean){
            callBack.onLoading(isLoading)
        }
        interface OnWebInteractionCallback
        {
            fun onSuccess(email : String)
            fun onError(errorMessage:String)
            fun onLoading(isLoading:Boolean)
        }
    }


}
