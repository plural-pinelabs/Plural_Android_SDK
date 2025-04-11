package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pinelabs.pluralsdk.data.model.FetchError
import com.pinelabs.pluralsdk.utils.Constants.Companion.API_ERROR
import com.pinelabs.pluralsdk.utils.Constants.Companion.API_INTERNET_MESSAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

inline fun <reified T> toResultFlow(
    context: Context,
    crossinline call: suspend () -> Response<T>?
): Flow<NetWorkResult<T>> {
    return flow {
        val isInternetConnected = Utils.hasInternetConnection(context)
        if (isInternetConnected) {
            emit(NetWorkResult.Loading(true))
            val c = call()
            c?.let { response ->
                try {
                    if (c.isSuccessful && c.body() != null) {
                        c.body()?.let {
                            emit(NetWorkResult.Success(it))
                        }
                    } else {
                        val type = object : TypeToken<FetchError>() {}.type
                        val errorResponse: FetchError? =
                            Gson().fromJson(response.errorBody()!!.charStream(), type)
                        emit(NetWorkResult.Error(null, errorResponse))
                    }
                } catch (e: Exception) {
                    val error = FetchError("-1", API_ERROR, null)
                    emit(NetWorkResult.Error(null, error))
                }
            }
        } else {
            val error = FetchError("-1", API_INTERNET_MESSAGE, null)
            emit(NetWorkResult.Error(null, error))
        }
    }.flowOn(Dispatchers.IO)
}

