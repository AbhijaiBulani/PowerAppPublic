package com.abhijai.powerfulandroidapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.abhijai.powerfulandroidapp.AppConstants.Companion.NETWORK_TIMEOUT
import com.abhijai.powerfulandroidapp.AppConstants.Companion.TESTING_CACHE_DELAY
import com.abhijai.powerfulandroidapp.AppConstants.Companion.TESTING_NETWORK_DELAY
import com.abhijai.powerfulandroidapp.ui.DataState
import com.abhijai.powerfulandroidapp.ui.Response
import com.abhijai.powerfulandroidapp.ui.ResponseType
import com.abhijai.powerfulandroidapp.util.*
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.abhijai.powerfulandroidapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

/**
 * ResponseObject from retrofit
 */
abstract class NetworkBoundResource<ResponseObject,CacheObject, ViewStateType>
    (
        isNetworkAvailable:Boolean,// is there a network Connection?
        isNetworkRequest:Boolean,
        shouldLoadFromCache:Boolean // load the data from the cache before making the network request
    )
{
    /**
     * The way this NetworkBoundResource is going to work is
     * -> for everyTransaction(n/w, db) a new NetworkBoundResource object is going to be created
     *    and then it will emmit the result.
     *
     * -> There will be exactly only one NetworkBoundResource for one transaction.
     */
    protected var result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job : CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true,cachedData = null))

        if (shouldLoadFromCache){
            val dbSource = loadFromCache()
            result.addSource(dbSource, Observer {
                result.removeSource(dbSource)
                result.value = DataState.loading(true,cachedData = it)
            })
        }

        if (isNetworkRequest)
        {
            if (isNetworkAvailable)
            {
                coroutineScope.launch {

                    /**
                     * The problem with job1 and job2 is we are executing them parallel.
                     *
                     * job is the parent of job1 and job2.
                     *
                     * job can't be get completed until job1 and job2 both get finished/completed.
                     *
                     * Let job1 takes 2 sec and job2 takes 6sec to get completed.
                     *
                     * So suppose we get response from web in 2sec in job1 and job1 get completed and
                     * we try to do job.complete(), then it will return us false because job2 is not
                     * finished yet.
                     *
                     * after 6 sec job2 also get completed and now if you do job.cancel() and u will get true.
                     *
                     * but problem in our case is that, job2 is used to find the NetworkTimeout so if we get
                     * data from server in job1 and we have good internet than also job2 is going to be executed
                     * and it will tell us to checkYourInternet connection.
                     *
                     * So the solution is -> Use GlobalScope sequentially to the this coroutineScope because than it will
                     * not be the child of job and you can cancel or complete job at any time. i.e :
                     *  We can complete job inside this scope(coroutineScope) because there is no child of job and cancel it in GlobalScope.
                     *
                     *  Remember job has dependency on its child to get completed, BUT job has no dependency on its child to get Canceled.
                     *
                     */
                    /*val job1 = launch {
                        Log.d("NetworkBoundResource", " Job1 started...")
                        withContext(Main){
                            createCallAndManageWebResult()
                        }
                    }

                    val job2 = launch {
                        Log.d("NetworkBoundResource", " Job2 started...")
                        manageNetworkTimeOut()
                    }*/

                    delay(TESTING_NETWORK_DELAY)
                    //Cannot invoke observeForever on a background thread
                    withContext(Main){
                        val apiResponse = createCall()
                        result.addSource(apiResponse){
                            result.removeSource(apiResponse)
                            coroutineScope.launch {
                                handleNetworkCall(it)
                            }
                        }
                    }
                }

                GlobalScope.launch {
                    Log.d("NetworkBoundResource", "----------Network Timeout GlobalScope STARTED WITH $NETWORK_TIMEOUT delay")
                    delay(NETWORK_TIMEOUT)
                    if (!job.isCompleted){
                        Log.e("NetworkBoundResource", "Network Timeout")
                        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                    }
                }
            }
            else{
                onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET,true,false)
            }
        }
        else{
            coroutineScope.launch {
                // fake delay for testing cache
                delay(TESTING_CACHE_DELAY)
                //View Data  From Cache Only and return
                Log.d("NetworkBoundResource", " createCacheRequestAndReturn ")
                createCacheRequestAndReturn()
            }
        }
    }

    private suspend fun createCallAndManageWebResult(){
        Log.d("NetworkBoundResource", "TESTING_NETWORK_DELAY value : $TESTING_NETWORK_DELAY ms")
        delay(TESTING_NETWORK_DELAY)
        Log.d("NetworkBoundResource", "createCallAndManageWebResult after $TESTING_NETWORK_DELAY ")
        val apiResponse = createCall()
        result.addSource(apiResponse){
            result.removeSource(apiResponse)
            coroutineScope.launch {
                handleNetworkCall(it)
            }
        }
    }

    private suspend fun manageNetworkTimeOut(){
        Log.e("NetworkBoundResource", "NETWORK_TIMEOUT value :  $NETWORK_TIMEOUT ms")
        delay(NETWORK_TIMEOUT)
        Log.d("NetworkBoundResource", "manageNetworkTimeOut -> IsJobCompleted : ${job.isCompleted}")
        if (!job.isCompleted){
            Log.d("NetworkBoundResource", "manageNetworkTimeOut :  JOB -> $job")
            Log.e("NetworkBoundResource", "Network Timeout after $NETWORK_TIMEOUT ms")
            job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when(response){
            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse ->{
                Log.e("NetworkBoundResource", " ${response.errorMessage}" )
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse ->{
                Log.e("NetworkBoundResource", "Request returned NOTHING (HTTP 204)" )
                onErrorReturn("HTTP 204. Returned nothing.", true, false)
            }
        }
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job{
        Log.d("NetworkBoundResource", "initNewJob called...")
        job = Job()
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler{
            override fun invoke(cause: Throwable?)
            {
                if (job.isCancelled){
                    Log.d("NetworkBoundResource", "invoke Job is Canceled -> Job : $job")
                    cause?.let {
                        onErrorReturn(it.message,false,true)
                    }?:onErrorReturn(ERROR_UNKNOWN,false,true)
                }
                else if (job.isCompleted){
                    Log.d("NetworkBoundResource", "invoke Job is complete -> Job : $job")
                    // Do nothing should be handled already
                }
                else if (job.isActive){
                    Log.d("NetworkBoundResource", "invoke Job is ACTIVE -> Job : $job")
                }
            }
        } )
        coroutineScope = CoroutineScope(IO+job)
        return job
    }

    protected fun onErrorReturn(errorMessage : String?, shouldUseDialog:Boolean, shouldUseToast:Boolean)
    {
        Log.e("NetworkBoundResource", "onErrorReturn, ErrorMessage : ${errorMessage}")
        var message = errorMessage
        var useDialog = shouldUseDialog
        var responseType : ResponseType = ResponseType.None()
        if (message == null){
            message = ERROR_UNKNOWN
        }
        else if(ErrorHandling.isNetworkError(message)){
            message = ERROR_CHECK_NETWORK_CONNECTION
            useDialog=false
        }
        if (shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if (useDialog){
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(DataState.error(response = Response(message=message,responseType = responseType)))
    }

    protected fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main) {
            Log.d("NetworkBoundResource", "onCompleteJob JOB -> $job")
            job.complete()
            //job.cancel()
            Log.d("NetworkBoundResource", "onCompleteJob -> IsJobCompleted : ${job.isCompleted}")
            Log.d("NetworkBoundResource", "***********************************")
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response : ApiSuccessResponse<ResponseObject>)

    abstract fun createCall() : LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache():LiveData<ViewStateType>

    abstract suspend fun updateLocalDB(cacheObject: CacheObject?)

    abstract fun setJob(job:Job)

}