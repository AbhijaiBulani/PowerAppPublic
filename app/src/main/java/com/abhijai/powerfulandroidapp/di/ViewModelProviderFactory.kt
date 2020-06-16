package com.abhijai.powerfulandroidapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ViewModelProviderFactory
@Inject
constructor(private val creators: Map< Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel> >) : ViewModelProvider.Factory
{
    // So here (?:) is known as Elvis operator(see notes).
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        //Map.Entry<Class<out ViewModel>,Provider<ViewModel>>
        // Above Entry is -> Represents a key/value pair held by a [Map].
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {mapEntry->
            // mapEntry : Map.Entry<Class<out ViewModel>,Provider<ViewModel>>
            modelClass.isAssignableFrom(mapEntry.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /*override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator = creators[modelClass]
        if (creator == null) { // if the viewmodel has not been created
            // loop through the allowable keys (aka allowed classes with the @ViewModelKey)
            for (entry in creators.entries) {
                // if it's allowed, set the Provider<ViewModel>
                if (modelClass.isAssignableFrom(entry.key)) {
                    creator = entry.value
                    break
                }
            }
        }
        // if this is not one of the allowed keys, throw exception
        requireNotNull(creator) { "unknown model class $modelClass" }
        // return the Provider
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }*/
}