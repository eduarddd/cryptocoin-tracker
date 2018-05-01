package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.nightlydev.cryptocointracker.extensions.apiSubscribe

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by edu on 10-3-18.
 *
 * Helper class use to execute API requests and encapsulate them in a {@link Resource}. This
 * Resource will be handed in a LiveData that can be observed using the method {@ling #asLiveData()}.
 */
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor() {

    private val result = MediatorLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading()
        val dbSource = this.loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    /**
     * Fetch the data from network and persist into DB and then
     * send it back to UI.
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val observable = createObservable()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { result.setValue(Resource.loading()) }

        observable?.apiSubscribe(object : Observer<RequestType> {
                    override fun onNext(response: RequestType) {
                        result.removeSource(dbSource)
                        response.apply {
                            processResponse(this)?.let { requestType -> {
                                Single.fromCallable {
                                    saveCallResult(requestType)
                                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()

                            } }
                            result.addSource(loadFromDb()) { newData -> setValue(Resource.success(newData)) }
                        }
                    }

                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        onFetchFailed()
                        result.addSource(dbSource) { result.setValue(Resource.error(error.message)) }
                    }
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable?) {}
                })
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    abstract fun createObservable(): Observable<RequestType>?

    @MainThread
    private fun setValue(newValue: Resource<ResultType?>) {
        if (result.value != newValue) result.value = newValue
    }

    protected fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType?>> {
        return result
    }

    @WorkerThread
    private fun processResponse(response: RequestType): RequestType? {
        return response
    }
}

