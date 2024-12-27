package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import com.pinelabs.pluralsdk.data.model.FetchError

class ApiResultHandler<T>(private val context: Context, private val onLoading: () -> Unit, private val onSuccess: (T?) -> Unit, private val onFailure: (FetchError?) -> Unit) {

    fun handleApiResult(result: NetWorkResult<T?>) {
        when (result.status) {
            ApiStatus.LOADING -> {
                onLoading()
            }
            ApiStatus.SUCCESS -> {
                onSuccess(result.data)
            }

            ApiStatus.ERROR -> {
                onFailure(result.message)
            }
        }
    }
}