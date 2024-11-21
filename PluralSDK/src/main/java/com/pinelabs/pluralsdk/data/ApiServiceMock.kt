package com.pinelabs.pluralsdk.data

import com.pinelabs.pluralsdk.data.model.CancelResponseData
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.model.RewardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceMock {
    @POST("fetch/data")
    suspend fun fetchData(@Query("token", encoded = true) token: String): Response<FetchResponse>

    @POST("process/payment")
    suspend fun processPayment(
        @Query("token", encoded = true) token: String,
        @Body request: ProcessPaymentRequest
    ): Response<ProcessPaymentResponse>

    @POST("check/rewards")
    suspend fun checkReward(
        @Query("token", encoded = true) token: String,
        @Body request: RewardRequest
    ): Response<RewardResponse>

    @GET("inquiry")
    suspend fun statusOfTransaction(
        @Query(
            "token",
            encoded = true
        ) token: String
    ): Response<TransactionStatusResponse>

    @POST("cancel")
    suspend fun cancelTransaction(
        @Query(
            "token",
            encoded = true
        ) token: String
    ): Response<CancelTransactionResponse>
}