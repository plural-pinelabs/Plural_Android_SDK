package com.pinelabs.pluralsdk.data.repository

import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.utils.RetrofitBuilder

class RemoteDataSource () {
    suspend fun fetchData(token:String) = RetrofitBuilder.apiService.fetchData(token)
    suspend fun processPayment(token:String, paymentData: ProcessPaymentRequest) = RetrofitBuilder.apiService.processPayment(token, paymentData)
    suspend fun reward(token:String, rewardData: RewardRequest) = RetrofitBuilder.apiService.checkReward(token, rewardData)
    suspend fun transactionStatus(token:String) = RetrofitBuilder.apiService.statusOfTransaction(token)
    suspend fun cancelTransaction(token:String) = RetrofitBuilder.apiService.cancelTransaction(token)
}