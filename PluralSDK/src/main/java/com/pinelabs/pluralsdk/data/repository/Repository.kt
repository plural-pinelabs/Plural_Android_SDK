package com.pinelabs.pluralsdk.data.repository

import android.content.Context
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import com.pinelabs.pluralsdk.data.utils.toResultFlow
import kotlinx.coroutines.flow.Flow

class Repository(private val remoteDataSource: RemoteDataSource) {

    suspend fun fetchData(context: Context, token: String): Flow<NetWorkResult<FetchResponse>> {
        return toResultFlow(context){
            remoteDataSource.fetchData(token)
        }
    }

    suspend fun processPayment(context: Context, token: String, paymentData: ProcessPaymentRequest): Flow<NetWorkResult<ProcessPaymentResponse>> {
        return toResultFlow(context){
            remoteDataSource.processPayment(token, paymentData)
        }
    }

}