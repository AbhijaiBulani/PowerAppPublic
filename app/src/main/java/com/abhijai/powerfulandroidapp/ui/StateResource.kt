package com.abhijai.powerfulandroidapp.ui

import android.util.Log

/**
 * Going to be used for show or hide progress bar
 */
data class Loading(val isLoading: Boolean)

/**
 * Parameters are wrapped in an Event class because we want to show it only once.
 */
data class Data<T>(val data: Event<T>?, val response: Event<Response>?)

/**
 * you can call this class any thing for e.g Error or etc we are calling it for uniqueness because by default kotlin also has Error classes.
 *
 * StateError parameter is not wrapped in Event because StateError is wrapped in Event (See class DataState)
 */
data class StateError(val response: Response)


data class Response(val message: String?, val responseType: ResponseType)


sealed class ResponseType{

    class Toast: ResponseType()

    class Dialog: ResponseType()

    class None: ResponseType()
}


/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        Log.d("EventO", "getContentIfNotHandled (line 46): $hasBeenHandled")
         if (hasBeenHandled)
         {
             return null
         }
         else
         {
            hasBeenHandled = true
            return content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    companion object{

        private val TAG: String = "AppDebug"

        /**
         * So if data is null just return null don't create any Event.
         */
        // we don't want to create an Event if data is null
        fun <T> dataEvent(data: T?): Event<T>?{
            data?.let {
                return Event(it)
            }
            return null
        }

        // we don't want an event if the response is null
        fun responseEvent(response: Response?): Event<Response>?{
            response?.let{
                return Event(response)
            }
            return null
        }
    }


}