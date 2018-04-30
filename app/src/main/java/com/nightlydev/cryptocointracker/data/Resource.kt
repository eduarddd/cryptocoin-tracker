package com.nightlydev.cryptocointracker.data

/**
 * Created by edu on 10-3-18.
 *
 * A generic class that holds a value with its loading status.
 * @param <ResultType> the type of the data stored by the Resource.
</T> */
data class Resource<ResultType>(var status: Status,
                                var data: ResultType? = null,
                                var message: String? = null) {

    companion object {
        /**
         * Creates [Resource] object with `SUCCESS` status and [data].
         */
        fun <ResultType> success(data: ResultType?): Resource<ResultType> = Resource(Status.SUCCESS, data = data)

        /**
         * Creates [Resource] object with `LOADING` status to notify
         * the UI to showing loading.
         */
        fun <ResultType> loading(data: ResultType? = null): Resource<ResultType> = Resource(Status.LOADING, data = data)

        /**
         * Creates [Resource] object with `ERROR` status and [message].
         */
        fun <ResultType> error(message: String?, data: ResultType? = null): Resource<ResultType> = Resource(Status.ERROR, message = message, data = data)
    }
}
