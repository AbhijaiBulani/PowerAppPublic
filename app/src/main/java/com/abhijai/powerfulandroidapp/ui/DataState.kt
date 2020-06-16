package com.abhijai.powerfulandroidapp.ui

/**
 * error and data parameters both of these are completely wrapped in Event class.
 * error is wrapped in this class and data is wrapped in StateResource class.
 *
 * B'coz of Event class -> you can look at data and error only at once.
 */
data class DataState<T>(
    var error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {

    companion object {

        fun <T> error(response: Response): DataState<T>
        {
            return DataState(error = Event(StateError(response)), loading = Loading(false), data = null)
        }

        fun <T> loading(isLoading: Boolean, cachedData: T? = null): DataState<T>
        {
            return DataState(error = null, loading = Loading(isLoading), data = Data(Event.dataEvent(cachedData), null))
        }

        fun <T> data(data: T? = null, response: Response? = null): DataState<T>
        {
            return DataState(error = null, loading = Loading(false), data = Data(Event.dataEvent(data), Event.responseEvent(response)))
        }
    }
}