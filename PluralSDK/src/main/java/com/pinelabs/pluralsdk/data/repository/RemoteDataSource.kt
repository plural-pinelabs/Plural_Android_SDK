package com.pinelabs.pluralsdk.data.repository

import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.utils.RetrofitBuilder

class RemoteDataSource () {
    suspend fun fetchData(token:String) = RetrofitBuilder.apiService.fetchData(token)
    suspend fun processPayment(token:String, paymentData: ProcessPaymentRequest) = RetrofitBuilder.apiService.processPayment(token, paymentData)
}