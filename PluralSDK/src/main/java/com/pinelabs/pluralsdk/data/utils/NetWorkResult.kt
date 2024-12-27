package com.pinelabs.pluralsdk.data.utils

import com.pinelabs.pluralsdk.data.model.FetchError

sealed class NetWorkResult <out T> (val status: ApiStatus, val data: T?, val message:FetchError?) {

    data class Success<out T>(val _data: T?): NetWorkResult<T>(status = ApiStatus.SUCCESS, data = _data, message = null)

    data class Error<out T>(val _data: T?,val exception: FetchError?): NetWorkResult<T>(status = ApiStatus.ERROR, data = _data, message = exception)

    data class Loading<out T>(val isLoading: Boolean): NetWorkResult<T>(status = ApiStatus.LOADING, data = null, message = null)
}
enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}